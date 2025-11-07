package control;

import entity.*;
import util.FileManager;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Manages withdrawal request operations including creation and staff review.
 * Uses singleton pattern to ensure single instance.
 */
public class WithdrawalManager {
    private static final String WITHDRAWALS_FILE = "withdrawals.dat";
    private List<WithdrawalRequest> withdrawalRequests;
    private static WithdrawalManager instance;

    /**
     * Private constructor for singleton pattern.
     * Initializes withdrawal requests list from file.
     */
    private WithdrawalManager() {
        this.withdrawalRequests = FileManager.loadFromFile(WITHDRAWALS_FILE);
    }

    /**
     * Returns the singleton instance of WithdrawalManager.
     *
     * @return WithdrawalManager instance
     */
    public static WithdrawalManager getInstance() {
        if (instance == null) {
            instance = new WithdrawalManager();
        }
        return instance;
    }

    /**
     * Saves all withdrawal requests to file.
     */
    public void saveWithdrawals() {
        FileManager.saveToFile(WITHDRAWALS_FILE, withdrawalRequests);
    }

    /**
     * Adds a new withdrawal request and saves to file.
     *
     * @param requestId unique request identifier
     * @param studentId student identifier
     * @param applicationId application identifier
     * @param reason reason for withdrawal
     */
    public void addWithdrawalRequest(String requestId, String studentId, String applicationId, String reason) {
        WithdrawalRequest request = new WithdrawalRequest(requestId, studentId,
                applicationId, reason);
        withdrawalRequests.add(request);
        saveWithdrawals();
    }

    /**
     * Gets a withdrawal request by ID.
     *
     * @param requestId request identifier
     * @return withdrawal request if found, null otherwise
     */
    public WithdrawalRequest getWithdrawalById(String requestId) {
        return withdrawalRequests.stream()
                .filter(w -> w.getRequestId().equals(requestId))
                .findFirst()
                .orElse(null);
    }

    /**
     * Updates a withdrawal request and saves to file.
     *
     * @param request updated withdrawal request
     */
    public void updateWithdrawalRequest(WithdrawalRequest request) {
        for (int i = 0; i < withdrawalRequests.size(); i++) {
            if (withdrawalRequests.get(i).getRequestId().equals(request.getRequestId())) {
                withdrawalRequests.set(i, request);
                saveWithdrawals();
                return;
            }
        }
    }

    /**
     * Gets all pending withdrawal requests.
     *
     * @return list of pending requests
     */
    public List<WithdrawalRequest> getPendingWithdrawals() {
        return withdrawalRequests.stream()
                .filter(w -> w.getStatus() == ApprovalStatus.PENDING)
                .collect(Collectors.toList());
    }

    /**
     * Gets all withdrawal requests for a student.
     *
     * @param studentId student identifier
     * @return list of withdrawal requests
     */
    public List<WithdrawalRequest> getWithdrawalsByStudent(String studentId) {
        return withdrawalRequests.stream()
                .filter(w -> w.getStudentId().equals(studentId))
                .collect(Collectors.toList());
    }

    /**
     * Checks if there is a pending withdrawal for an application.
     *
     * @param applicationId application identifier
     * @return true if pending withdrawal exists, false otherwise
     */
    public boolean hasPendingWithdrawal(String applicationId) {
        return withdrawalRequests.stream()
                .anyMatch(w -> w.getApplicationId().equals(applicationId) &&
                        w.getStatus() == ApprovalStatus.PENDING);
    }

    /**
     * Reviews and processes a withdrawal request.
     * If approved, removes application and updates placement/slots if confirmed.
     *
     * @param request withdrawal request to review
     * @param staffId staff member reviewing
     * @param decision 1 for approve, 2 for reject
     */
    public void reviewWithdrawal(WithdrawalRequest request, String staffId, int decision) {
        ApplicationManager applicationManager = ApplicationManager.getInstance();
        UserManager userManager = UserManager.getInstance();
        InternshipManager internshipManager = InternshipManager.getInstance();
        switch (decision) {
            case 1:
                request.setStatus(ApprovalStatus.APPROVED);
                request.setReviewedByStaffId(staffId);
                request.setReviewDate(LocalDateTime.now());
                updateWithdrawalRequest(request);
                Application app = applicationManager.getApplicationById(request.getApplicationId());
                
                if (app == null) {
                    return;
                }
                
                boolean wasConfirmed = (app.getStatus().equals(ApplicationStatus.CONFIRMED));
                String internshipId = app.getInternshipId();

                applicationManager.removeApplication(app);

                if (wasConfirmed) {
                    Student student = (Student) userManager.getUserById(request.getStudentId());
                    if (student == null) {
                        return;
                    }
                    student.setConfirmedPlacementId(null);
                    userManager.updateUser(student);

                    Internship internship = internshipManager.getInternshipById(internshipId);
                    if (internship == null) {
                        return;
                    }
                    internship.decrementConfirmedSlots();
                    internshipManager.updateInternship(internship);
                }
                break;
            
            case 2:
                request.setStatus(ApprovalStatus.REJECTED);
                request.setReviewedByStaffId(staffId);
                request.setReviewDate(LocalDateTime.now());
                updateWithdrawalRequest(request);
                break;
        
            default:
                break;
        }
    }
}
