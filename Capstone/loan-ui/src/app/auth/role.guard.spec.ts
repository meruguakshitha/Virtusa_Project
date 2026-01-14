import { TestBed } from '@angular/core/testing';
import { Router } from '@angular/router';
import { RoleGuard } from './role.guard';

describe('RoleGuard', () => {
  let guard: RoleGuard;
  let router: jasmine.SpyObj<Router>;

  beforeEach(() => {
    router = jasmine.createSpyObj('Router', ['navigate']);

    TestBed.configureTestingModule({
      providers: [
        RoleGuard,
        { provide: Router, useValue: router }
      ]
    });

    guard = TestBed.inject(RoleGuard);
  });

  it('should allow access when role matches', () => {
    localStorage.setItem('role', 'ADMIN');

    const result = guard.canActivate({ data: { roles: ['ADMIN'] } } as any);

    expect(result).toBe(true);
  });

  it('should block access when role does not match', () => {
    localStorage.setItem('role', 'USER');

    const result = guard.canActivate({ data: { roles: ['ADMIN'] } } as any);

    expect(result).toBe(false);
    expect(router.navigate).toHaveBeenCalledWith(['/unauthorized']);
  });
});
