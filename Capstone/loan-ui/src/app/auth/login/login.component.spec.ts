import { ComponentFixture, TestBed } from '@angular/core/testing';
import { of, throwError } from 'rxjs';

import { LoginComponent } from './login.component';
import { AuthService } from '../auth.service';

describe('LoginComponent', () => {
  let component: LoginComponent;
  let fixture: ComponentFixture<LoginComponent>;
  let authServiceSpy: jasmine.SpyObj<AuthService>;

  beforeEach(async () => {
    authServiceSpy = jasmine.createSpyObj('AuthService', [
      'login',
      'handleLoginSuccess'
    ]);

    await TestBed.configureTestingModule({
      imports: [LoginComponent], // ✅ standalone component
      providers: [
        { provide: AuthService, useValue: authServiceSpy }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(LoginComponent);
    component = fixture.componentInstance;
  });

  it('should create component', () => {
    expect(component).toBeTruthy();
  });

  // ==================== SUCCESS ====================

  it('should call auth.login and handle success', () => {
    const mockResponse = {
      token: 'fake-token',
      role: 'USER'
    };

    authServiceSpy.login.and.returnValue(of(mockResponse));

    component.email = 'rm@bank.com';
    component.password = 'password';

    component.login();

    expect(authServiceSpy.login)
      .toHaveBeenCalledWith('rm@bank.com', 'password');

    expect(authServiceSpy.handleLoginSuccess)
      .toHaveBeenCalledWith(mockResponse);

    expect(component.error).toBe('');
  });

  // ==================== ERROR ====================

  it('should show error message on login failure', () => {
    authServiceSpy.login.and.returnValue(
      throwError(() => new Error('Unauthorized'))
    );

    component.email = 'rm@bank.com';
    component.password = 'wrong';

    component.login();

    expect(component.error).toBe('Invalid credentials');
  });
});
