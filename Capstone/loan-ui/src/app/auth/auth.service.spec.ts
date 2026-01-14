import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { AuthService } from './auth.service';

describe('AuthService', () => {
  let service: AuthService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [AuthService]
    });

    service = TestBed.inject(AuthService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
    localStorage.clear();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should login and store JWT token', () => {
    const mockResponse = {
      token: 'fake-jwt-token',
      role: 'USER'
    };

    service.login('rm@bank.com', 'password').subscribe(res => {
      expect(res.token).toBe('fake-jwt-token');
      expect(localStorage.getItem('token')).toBe('fake-jwt-token');
      expect(localStorage.getItem('role')).toBe('USER');
    });

    const req = httpMock.expectOne('/api/auth/login');
    expect(req.request.method).toBe('POST');
    expect(req.request.body).toEqual({
      email: 'rm@bank.com',
      password: 'password'
    });

    req.flush(mockResponse);
  });

  it('should handle login error', () => {
    service.login('rm@bank.com', 'wrong').subscribe({
      next: () => fail('should fail'),
      error: (error) => {
        expect(error.status).toBe(401);
      }
    });

    const req = httpMock.expectOne('/api/auth/login');
    req.flush(
      { message: 'Invalid credentials' },
      { status: 401, statusText: 'Unauthorized' }
    );
  });

  it('should clear token on logout', () => {
    localStorage.setItem('token', 'abc');
    localStorage.setItem('role', 'USER');

    service.logout();

    expect(localStorage.getItem('token')).toBeNull();
    expect(localStorage.getItem('role')).toBeNull();
  });

  it('should return true when token exists', () => {
    localStorage.setItem('token', 'abc');
    expect(service.isLoggedIn()).toBeTrue();
  });

  it('should return false when token does not exist', () => {
    localStorage.clear();
    expect(service.isLoggedIn()).toBeFalse();
  });
});
