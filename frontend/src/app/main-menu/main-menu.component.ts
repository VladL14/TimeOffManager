import { Component } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { UserService} from '../user.service';
import { NgIf, NgForOf } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';

@Component({
  selector: 'app-main-menu',
  standalone: true,
  imports: [NgIf, NgForOf, FormsModule, RouterModule],
  templateUrl: './main-menu.component.html',
  styleUrl: './main-menu.component.scss'
})
export class MainMenuComponent {
  vacationBalance: number | undefined;
  sickLeaveBalance: number | undefined;
  unpaidLeaveBalance: number | undefined;
  showDashboard = false;
  showForm = false;
  showRequests = false;
  users: any[] = [];
  showUsers = false;
  showUserForm = false;
  leaveRequests: any[] = [];
  allRequests: any[] = [];
  selectedRequest: any = null;

  newUser = {
    name: '',
    email: '',
    role: '',
  }

  predefinedLeaveTypes = [
    { name: 'Vacation' },
    { name: 'Sick Leave' },
    { name: 'Unpaid' },
  ];

  newLeaveRequest = {
    leaveTypeName: '',
    startDate: '',
    endDate: '',
    notes: '',
    status: 'PENDING'
  };

  constructor(
    public userService: UserService,
    private http: HttpClient
  ) {}
  ngOnInit() {
    this.userService.loadUser().subscribe(() => {
      this.userService.getVacationBalance(this.userService.getUser()).subscribe(balance => {
        this.vacationBalance = balance;
      });
      this.userService.getSickBalance(this.userService.getUser()).subscribe(balance => {
        this.sickLeaveBalance = balance;
      });
      this.userService.getUnpaidBalance(this.userService.getUser()).subscribe(balance => {
        this.unpaidLeaveBalance = balance;
      });

    if (this.userService.getRole() === 'ADMIN') {
      this.loadAllRequests();
    }
    });
  }


  goBackToMenu() {
  this.showDashboard = false;
  this.showForm = false;
  this.showRequests = false;
  this.selectedRequest = null;
  this.leaveRequests = [];
}

  loadAllRequests() {
    this.http.get<any[]>('/api/leaverequests').subscribe(data => {
      this.allRequests = data;
      this.allRequests.forEach(request => {
      this.userService.getUserById(request.userId).subscribe(user => {
        request.userName = user.name;
      });
    });
    });
  }

  goToDashboard() {
    this.showDashboard = true;
  }

  toggleShowForm() {
    this.showForm = !this.showForm;
  }

  toggleShowRequests() {
    if (!this.showRequests) {
      if(this.userService.getRole() === 'ADMIN') {
        this.loadAllRequests();
      }else{
      this.getMyLeaveRequests();
    }
  }
    this.showRequests = !this.showRequests;
  }

  getMyLeaveRequests() {
    const currentUserId = this.userService.getUser();
    this.http.get<any[]>(`/api/leaverequests/user/${currentUserId}`).subscribe(data => {
      this.leaveRequests = data;
    });
  }

  formatDate(dateString: string): string {
    const date = new Date(dateString);
    const zi = String(date.getDate()).padStart(2, '0');
    const luna = String(date.getMonth() + 1).padStart(2, '0');
    const an = date.getFullYear();
    return `${zi}.${luna}.${an}`;
  }

  formatForInput(dateString: string): string {
    const date = new Date(dateString);
    return date.toISOString().split('T')[0];
  }

  submitNewRequest() {
    const currentUserId = this.userService.getUser();

    const requestData = {
      leaveTypeName: this.newLeaveRequest.leaveTypeName,
      startDate: this.newLeaveRequest.startDate,
      endDate: this.newLeaveRequest.endDate,
      notes: this.newLeaveRequest.notes,
      status: 'PENDING',
      userId: currentUserId
    };

    this.http.post('/api/leaverequests', requestData).subscribe({
      next: () => {
        alert('The request was sent successfully!');
        this.getMyLeaveRequests();
        this.resetForm();
      },
      error: () => {
        alert('Error while sending the request');
      }
    });
  }

  selectRequestForEdit(request: any) {
    this.selectedRequest = {
      id: request.id,
      leaveTypeName: request.leaveTypeName,
      startDate: request.startDate,
      endDate: request.endDate,
      notes: request.notes,
      status: request.status,
      userId: request.userId
    };
  }

  updateLeaveRequest() {
    if (!this.selectedRequest?.id) return;

    this.http.put(`/api/leaverequests/${this.selectedRequest.id}`, this.selectedRequest).subscribe({
      next: () => {
        alert('Request updated successfully!');
        this.getMyLeaveRequests();
        this.selectedRequest = null;
      },
      error: () => {
        alert('Error while updating the request');
      }
    });
  }

  cancelEdit() {
    this.selectedRequest = null;
  }

  deleteLeaveRequest(requestId: number) {
    if (confirm('Are you sure you want to delete this request?')) {
      this.http.delete(`/api/leaverequests/${requestId}/delete`).subscribe({
        next: () => {
          alert('Request deleted successfully!');
          this.getMyLeaveRequests();
        },
        error: () => {
          alert('Error while deleting the request');
        }
      });
    }
  }
    resetForm() {
    this.newLeaveRequest = {
      leaveTypeName: '',
      startDate: '',
      endDate: '',
      notes: '',
      status: 'PENDING'
    };
  }

  approveLeaveRequest(requestId: number) {
    const currentId = this.userService.getUser();
    this.http.put(`/api/leaverequests/${requestId}/approve?givenId=${currentId}`, {}).subscribe({
      next: () => {
        alert('Request approved successfully!');
        this.loadAllRequests();
      },
      error: () => {
        alert('Error while approving the request');
      }
    });
  }
  rejectLeaveRequest(requestId: number) {
    const currentId = this.userService.getUser();
    this.http.put(`/api/leaverequests/${requestId}/reject?givenId=${currentId}`, {}).subscribe({
      next: () => {
        alert('Request rejected successfully!');
        this.loadAllRequests();
      },
      error: () => {
        alert('Error while rejecting the request');
      }
    });
  }
  toggleShowUsers() {
    if (!this.showUsers) {
      this.loadUsers();
    }
    this.showUsers = !this.showUsers;
  }
  toggleShowUserForm() {
    this.showUserForm = !this.showUserForm;
  }
  loadUsers() {
    this.http.get<any[]>('/api/users').subscribe(data => {
      this.users = data;
      this.users.forEach(user => {
        if(user.isActive === true){
        this.userService.getVacationBalance(user.id).subscribe(balance => {
          user.vacationBalance = balance;
        });
        this.userService.getSickBalance(user.id).subscribe(balance => {
          user.sickBalance = balance;
        });
        this.userService.getUnpaidBalance(user.id).subscribe(balance => {
          user.unpaidBalance = balance;
        });
      }
      });
    });
  }
  createUser() {
    this.http.post('/api/users/createUser', this.newUser).subscribe(() => {
      alert('User created successfully!');
      this.newUser = {
        name: '',
        email: '',
        role: '',
      };
      this.loadUsers();
    });
  }
  deleteUser(userId: number) {
    if(confirm('Are you sure you want to delete this user?')){
      this.http.delete(`/api/users/deleteUser?id=${userId}`).subscribe(() => {
        alert('User deleted successfully!');
        
      });
      this.loadUsers();
    }
  }
  updateBalances(userId: number, vacation: number, sickLeave:number, unpaid: number) {
    this.http.put(`/api/leavetypes/user/${userId}/vacation/balance?newBalance=${vacation}`, {}).subscribe();
    this.http.put(`/api/leavetypes/user/${userId}/sick_leave/balance?newBalance=${sickLeave}`, {}).subscribe();
    this.http.put(`/api/leavetypes/user/${userId}/unpaid/balance?newBalance=${unpaid}`, {}).subscribe();
    alert('Balances updated successfully!');
    this.loadUsers();
  }



}