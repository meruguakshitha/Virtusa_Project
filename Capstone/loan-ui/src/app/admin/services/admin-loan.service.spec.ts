import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { AdminLoanService } from './admin-loan.service';

describe('AdminLoanService', () => {
  let service: AdminLoanService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [AdminLoanService]
    });

    service = TestBed.inject(AdminLoanService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should fetch loans list', () => {
    const mockResponse = {
      content: [
        { id: '1', clientName: 'ABC Corp' }
      ]
    };

    service.getLoans().subscribe(res => {
      expect(res.content.length).toBe(1);
      expect(res.content[0].clientName).toBe('ABC Corp');
    });

    const req = httpMock.expectOne('http://localhost:8081/api/admin/loans');
    expect(req.request.method).toBe('GET');
    req.flush(mockResponse);
  });

  

});
