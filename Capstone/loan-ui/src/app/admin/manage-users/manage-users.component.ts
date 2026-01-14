import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { AdminUserService } from '../services/admin-user.service';

@Component({
  standalone: true,
  imports: [CommonModule, FormsModule],
  selector: 'app-manage-users',
  templateUrl: './manage-users.component.html',
  styleUrls: ['./manage-users.component.css']
})
export class ManageUsersComponent implements OnInit {

  users: any[] = [];

  newUser = {
    email: '',
    password: '',
    role: 'USER'
  };

  constructor(private userService: AdminUserService) {}

  ngOnInit(): void {
  this.loadUsers();

  window.addEventListener('storage', () => {
    this.loadUsers(); // 🔥 refresh users when loan changes
  });
}


  loadUsers() {
    this.userService.getAllUsers().subscribe(res => {
      this.users = res;
    });
  }

  createUser() {
    this.userService.createUser(this.newUser).subscribe(() => {
      this.loadUsers();
      this.newUser = { email: '', password: '', role: 'USER' };
    });
  }

  toggle(user: any) {
    this.userService
      .updateStatus(user.id, !user.active)
      .subscribe(() => this.loadUsers());
  }

  approveUser(user: any) {
  this.userService.updateStatus(user.id, true).subscribe(() => {
    user.active = true;
  });
}

rejectUser(user: any) {
  this.userService.updateStatus(user.id, false).subscribe(() => {
    user.active = false;
  });
}

}
