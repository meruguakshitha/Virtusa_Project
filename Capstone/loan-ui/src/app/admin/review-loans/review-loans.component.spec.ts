import { ComponentFixture, TestBed } from '@angular/core/testing';
import { of } from 'rxjs';

import { ReviewLoansComponent } from './review-loans.component';
import { AdminLoanService } from '../services/admin-loan.service';

describe('ReviewLoansComponent', () => {
  let component: ReviewLoansComponent;
  let fixture: ComponentFixture<ReviewLoansComponent>;
  let loanServiceSpy: jasmine.SpyObj<AdminLoanService>;

  beforeEach(async () => {
    loanServiceSpy = jasmine.createSpyObj('AdminLoanService', [
      'getLoans',
      'changeStatus'
    ]);

    await TestBed.configureTestingModule({
      imports: [ReviewLoansComponent], // ✅ standalone
      providers: [
        { provide: AdminLoanService, useValue: loanServiceSpy }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(ReviewLoansComponent);
    component = fixture.componentInstance;
  });

  it('should create component', () => {
    expect(component).toBeTruthy();
  });

  // ==================== INIT ====================

  it('should fetch loans on init', () => {
    const mockResponse = {
      content: [
        { id: '1', clientName: 'OmniTech', status: 'SUBMITTED' }
      ]
    };

    loanServiceSpy.getLoans.and.returnValue(of(mockResponse));

    component.ngOnInit();

    expect(loanServiceSpy.getLoans).toHaveBeenCalled();
    expect(component.loans.length).toBe(1);
    expect(component.loading).toBeFalse();
  });

  // ==================== FETCH LOANS ====================

  it('should fetch loans and set loading false', () => {
    const mockResponse = {
      content: [
        { id: '1', status: 'SUBMITTED' },
        { id: '2', status: 'UNDER_REVIEW' }
      ]
    };

    loanServiceSpy.getLoans.and.returnValue(of(mockResponse));

    component.fetchLoans();

    expect(loanServiceSpy.getLoans).toHaveBeenCalled();
    expect(component.loans.length).toBe(2);
    expect(component.loading).toBeFalse();
  });

  // ==================== MOVE TO REVIEW ====================

  it('should move loan to UNDER_REVIEW', () => {
    loanServiceSpy.changeStatus.and.returnValue(of({}));
    loanServiceSpy.getLoans.and.returnValue(of({ content: [] }));

    component.moveToReview('123');

    expect(loanServiceSpy.changeStatus)
      .toHaveBeenCalledWith('123', 'UNDER_REVIEW', 'Moved to review');

    expect(loanServiceSpy.getLoans).toHaveBeenCalled();
  });

  // ==================== APPROVE ====================

  it('should approve loan', () => {
    loanServiceSpy.changeStatus.and.returnValue(of({}));
    loanServiceSpy.getLoans.and.returnValue(of({ content: [] }));

    component.approve('999');

    expect(loanServiceSpy.changeStatus)
      .toHaveBeenCalledWith('999', 'APPROVED', 'Approved by admin');

    expect(loanServiceSpy.getLoans).toHaveBeenCalled();
  });

  // ==================== REJECT ====================

  it('should reject loan', () => {
    loanServiceSpy.changeStatus.and.returnValue(of({}));
    loanServiceSpy.getLoans.and.returnValue(of({ content: [] }));

    component.reject('888');

    expect(loanServiceSpy.changeStatus)
      .toHaveBeenCalledWith('888', 'REJECTED', 'Rejected by admin');

    expect(loanServiceSpy.getLoans).toHaveBeenCalled();
  });
});
