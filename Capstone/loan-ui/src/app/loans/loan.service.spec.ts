import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { LoanService } from './loan.service';

describe('LoanService', () => {
  let service: LoanService;
  let httpMock: HttpTestingController;

  const baseUrl = 'http://localhost:8081/api/loans';

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [LoanService]
    });

    service = TestBed.inject(LoanService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  // ===================== USER =====================

  it('should fetch my loans', () => {
    const mockLoans = [
      { clientName: 'OmniTech', status: 'DRAFT' },
      { clientName: 'Naga Corp', status: 'SUBMITTED' }
    ];

    service.getMyLoans().subscribe(res => {
      expect(res.length).toBe(2);
      expect(res[0].clientName).toBe('OmniTech');
    });

    const req = httpMock.expectOne(`${baseUrl}?my=true`);
    expect(req.request.method).toBe('GET');
    req.flush(mockLoans);
  });

  it('should create a loan', () => {
    const payload = {
      clientName: 'OmniTech',
      loanType: 'TermLoan',
      requestedAmount: 5000000
    };

    service.create(payload).subscribe(res => {
      expect(res.clientName).toBe('OmniTech');
    });

    const req = httpMock.expectOne(baseUrl);
    expect(req.request.method).toBe('POST');
    expect(req.request.body).toEqual(payload);
    req.flush(payload);
  });

  it('should fetch loan by id', () => {
    const loanId = '123';

    service.getById(loanId).subscribe(res => {
      expect(res.clientName).toBe('OmniTech');
    });

    const req = httpMock.expectOne(`${baseUrl}/${loanId}`);
    expect(req.request.method).toBe('GET');
    req.flush({ clientName: 'OmniTech' });
  });

  // ===================== ADMIN =====================

  it('should fetch all loans (admin)', () => {
    const mockLoans = [{ clientName: 'AdminView Loan' }];

    service.getAllLoans().subscribe(res => {
      expect(res.length).toBe(1);
    });

    const req = httpMock.expectOne(baseUrl);
    expect(req.request.method).toBe('GET');
    req.flush(mockLoans);
  });

  it('should approve loan', () => {
    const loanId = '999';

    service.approveLoan(loanId).subscribe(res => {
      expect(res).toBeUndefined();
    });

    const req = httpMock.expectOne(`${baseUrl}/${loanId}/approve`);
    expect(req.request.method).toBe('PATCH');
    expect(req.request.body).toEqual({});
    req.flush(null);
  });

  it('should reject loan', () => {
    const loanId = '888';

    service.rejectLoan(loanId).subscribe(res => {
      expect(res).toBeUndefined();
    });

    const req = httpMock.expectOne(`${baseUrl}/${loanId}/reject`);
    expect(req.request.method).toBe('PATCH');
    expect(req.request.body).toEqual({});
    req.flush(null);
  });

  // ===================== ERROR =====================

  it('should handle error while fetching loans', () => {
    service.getAllLoans().subscribe({
      next: () => fail('should fail'),
      error: (error) => {
        expect(error.status).toBe(500);
      }
    });

    const req = httpMock.expectOne(baseUrl);
    req.flush(
      { message: 'Server error' },
      { status: 500, statusText: 'Internal Server Error' }
    );
  });
});
