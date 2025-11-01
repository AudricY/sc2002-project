package control;

import entity.*;
import util.FileManager;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class WithdrawalManager {
    private static final String WITHDRAWALS_FILE = "withdrawals.dat";
    private List<WithdrawalRequest> withdrawalRequests;
    private static WithdrawalManager instance;

    private WithdrawalManager() {
        this.withdrawalRequests = FileManager.loadFromFile(WITHDRAWALS_FILE);
    }

    public static WithdrawalManager getInstance() {
        if (instance == null) {
            instance = new WithdrawalManager();
        }
        return instance;
    }

    public void saveWithdrawals() {
        FileManager.saveToFile(WITHDRAWALS_FILE, withdrawalRequests);
    }

    public void addWithdrawalRequest(String requestId, String studentId, String applicationId, String reason) {
        WithdrawalRequest request = new WithdrawalRequest(requestId, studentId,
                applicationId, reason);
        withdrawalRequests.add(request);
        saveWithdrawals();
    }

    public WithdrawalRequest getWithdrawalById(String requestId) {
        return withdrawalRequests.stream()
                .filter(w -> w.getRequestId().equals(requestId))
                .findFirst()
                .orElse(null);
    }

    public void updateWithdrawalRequest(WithdrawalRequest request) {
        for (int i = 0; i < withdrawalRequests.size(); i++) {
            if (withdrawalRequests.get(i).getRequestId().equals(request.getRequestId())) {
                withdrawalRequests.set(i, request);
                saveWithdrawals();
                return;
            }
        }
    }

    public List<WithdrawalRequest> getPendingWithdrawals() {
        return withdrawalRequests.stream()
                .filter(w -> w.getStatus() == ApprovalStatus.PENDING)
                .collect(Collectors.toList());
    }

    public List<WithdrawalRequest> getWithdrawalsByStudent(String studentId) {
        return withdrawalRequests.stream()
                .filter(w -> w.getStudentId().equals(studentId))
                .collect(Collectors.toList());
    }

    public boolean hasPendingWithdrawal(String applicationId) {
        return withdrawalRequests.stream()
                .anyMatch(w -> w.getApplicationId().equals(applicationId) &&
                        w.getStatus() == ApprovalStatus.PENDING);
    }

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
                
                boolean wasConfirmed = (app.getStatus().equals(ApplicationStatus.CONFIRMED));
                String internshipId = app.getInternshipId();

                applicationManager.removeApplication(app);

                if (wasConfirmed) {
                    Student student = (Student) userManager.getUserById(request.getStudentId());
                    student.setConfirmedPlacementId(null);
                    userManager.updateUser(student);

                    Internship internship = internshipManager.getInternshipById(internshipId);
                    internship.decrementConfirmedSlots();
                    internshipManager.updateInternship(internship);
                }
                break;
            
            case 2:
                request.setStatus(ApprovalStatus.REJECTED);
                request.setReviewedByStaffId(staffId);
                request.setReviewDate(LocalDateTime.now());
                updateWithdrawalRequest(request);
        
            default:
                break;
        }
    }
}
