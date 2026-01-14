import { ComponentFixture, TestBed } from '@angular/core/testing';
import { of } from 'rxjs';

import { ManageUsersComponent } from './manage-users.component';
import { AdminUserService } from '../services/admin-user.service';

describe('ManageUsersComponent', () => {
  let component: ManageUsersComponent;
  let fixture: ComponentFixture<ManageUsersComponent>;
  let userServiceSpy: jasmine.SpyObj<AdminUserService>;

  beforeEach(async () => {
    userServiceSpy = jasmine.createSpyObj('AdminUserService', [
      'getAllUsers',
      'createUser',
      'updateStatus'
    ]);

    await TestBed.configureTestingModule({
      imports: [ManageUsersComponent], // ✅ standalone component
      providers: [
        { provide: AdminUserService, useValue: userServiceSpy }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(ManageUsersComponent);
    component = fixture.componentInstance;
  });

  it('should create component', () => {
    expect(component).toBeTruthy();
  });

  // ==================== INIT ====================

  it('should load users on init', () => {
    const mockUsers = [
      { id: '1', email: 'admin@bank.com', active: true }
    ];

    userServiceSpy.getAllUsers.and.returnValue(of(mockUsers));

    component.ngOnInit();

    expect(userServiceSpy.getAllUsers).toHaveBeenCalled();
    expect(component.users.length).toBe(1);
    expect(component.users[0].email).toBe('admin@bank.com');
  });

  // ==================== LOAD USERS ====================

  it('should load users', () => {
    const mockUsers = [
      { id: '1', email: 'rm@bank.com', active: true },
      { id: '2', email: 'user@bank.com', active: false }
    ];

    userServiceSpy.getAllUsers.and.returnValue(of(mockUsers));

    component.loadUsers();

    expect(userServiceSpy.getAllUsers).toHaveBeenCalled();
    expect(component.users.length).toBe(2);
  });

  // ==================== CREATE USER ====================

  it('should create user and reset form', () => {
    userServiceSpy.createUser.and.returnValue(of({}));
    userServiceSpy.getAllUsers.and.returnValue(of([]));

    component.newUser = {
      email: 'new@bank.com',
      password: 'password',
      role: 'USER'
    };

    component.createUser();

    expect(userServiceSpy.createUser).toHaveBeenCalledWith(component.newUser);
    expect(userServiceSpy.getAllUsers).toHaveBeenCalled();
    expect(component.newUser.email).toBe('');
    expect(component.newUser.password).toBe('');
    expect(component.newUser.role).toBe('USER');
  });

  // ==================== TOGGLE STATUS ====================

  it('should toggle user status', () => {
    const user = { id: '1', active: true };

    userServiceSpy.updateStatus.and.returnValue(of({}));
    userServiceSpy.getAllUsers.and.returnValue(of([]));

    component.toggle(user);

    expect(userServiceSpy.updateStatus)
      .toHaveBeenCalledWith('1', false);
    expect(userServiceSpy.getAllUsers).toHaveBeenCalled();
  });

  // ==================== APPROVE USER ====================

  it('should approve user', () => {
    const user = { id: '1', active: false };

    userServiceSpy.updateStatus.and.returnValue(of({}));

    component.approveUser(user);

    expect(userServiceSpy.updateStatus)
      .toHaveBeenCalledWith('1', true);
    expect(user.active).toBeTrue();
  });

  // ==================== REJECT USER ====================

  it('should reject user', () => {
    const user = { id: '1', active: true };

    userServiceSpy.updateStatus.and.returnValue(of({}));

    component.rejectUser(user);

    expect(userServiceSpy.updateStatus)
      .toHaveBeenCalledWith('1', false);
    expect(user.active).toBeFalse();
  });
});
