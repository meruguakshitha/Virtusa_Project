import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { AdminUserService } from './admin-user.service';

describe('AdminUserService', () => {
  let service: AdminUserService;
  let httpMock: HttpTestingController;

  const baseUrl = 'http://localhost:8081/api/admin/users';

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [AdminUserService]
    });

    service = TestBed.inject(AdminUserService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  // ✅ GET USERS
  it('should fetch all users', () => {
    const mockUsers = [
      { email: 'admin@bank.com', role: 'ADMIN', active: true },
      { email: 'rm@bank.com', role: 'USER', active: true }
    ];

    service.getAllUsers().subscribe(users => {
      expect(users.length).toBe(2);
      expect(users[0].email).toBe('admin@bank.com');
    });

    const req = httpMock.expectOne(baseUrl);
    expect(req.request.method).toBe('GET');
    req.flush(mockUsers);
  });

  // ✅ CREATE USER
  it('should create a new user', () => {
    const newUser = {
      email: 'new@bank.com',
      password: 'password',
      role: 'USER'
    };

    service.createUser(newUser).subscribe(res => {
      expect(res).toBeTruthy();
    });

    const req = httpMock.expectOne(baseUrl);
    expect(req.request.method).toBe('POST');
    expect(req.request.body).toEqual(newUser);
    req.flush({ success: true });
  });

  // ✅ UPDATE STATUS
  it('should update user status', () => {
    const userId = '123';
    const active = false;

    service.updateStatus(userId, active).subscribe(res => {
      expect(res).toBeTruthy();
    });

    const req = httpMock.expectOne(`${baseUrl}/${userId}/status`);
    expect(req.request.method).toBe('PUT');
    expect(req.request.body).toEqual({ active });

    req.flush({ success: true });
  });

  // ✅ HANDLE ERROR
  it('should handle error when fetching users', () => {
    service.getAllUsers().subscribe({
      next: () => fail('should fail'),
      error: (error) => {
        expect(error.status).toBe(403);
      }
    });

    const req = httpMock.expectOne(baseUrl);
    req.flush(
      { message: 'Forbidden' },
      { status: 403, statusText: 'Forbidden' }
    );
  });
});
