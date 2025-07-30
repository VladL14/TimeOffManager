import { Component } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { UserService} from '../user.service';
import { AsyncPipe, NgIf, NgForOf } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { Store } from '@ngxs/store';
import { MainMenuState } from '../state/main-menu.state';
import { Observable } from 'rxjs';
import { GetAllLeaveTypes, GetMyLeaveRequests, SetAllRequests, SetLeaveRequests, SetSelectedRequest, GetAllRequests, GetSubordinatesRequests } from '../state/main-menu.actions';

@Component({
  selector: 'app-main-menu',
  standalone: true,
  imports: [NgIf, NgForOf, FormsModule, RouterModule, AsyncPipe],
  templateUrl: './main-menu.component.html',
  styleUrl: './main-menu.component.scss'
})
export class MainMenuComponent {
  showDashboard = false;
  showForm = false;
  showRequests = false;
  users: any[] = [];
  showUsers = false;
  showUserForm = false;
  leaveRequests$: Observable<any[]>;
  selectedRequest$: Observable<any | null>;
  allRequests$: Observable<any[]>;
  leaveBalances$: Observable<{ [key: string]: number }>;
  newUser = { name: '', email: '', role: '' };
  predefinedLeaveTypes = [{ name: 'Vacation' }, { name: 'Sick Leave' }, { name: 'Unpaid' }];
  newLeaveRequest = { leaveTypeName: '', startDate: '', endDate: '', notes: '', status: 'PENDING' };

  constructor(
    public userService: UserService,
    private http: HttpClient,
    private store: Store
  ) {
    this.leaveRequests$ = this.store.select(MainMenuState.getLeaveRequests);
    this.selectedRequest$ = this.store.select(MainMenuState.getSelectedRequest);
    this.allRequests$ = this.store.select(MainMenuState.getAllRequests);
    this.leaveBalances$ = this.store.select(MainMenuState.getLeaveBalances);
  }

  ngOnInit() {
    this.userService.loadUser().subscribe(() => {
      const userId = this.userService.getUser();
      this.store.dispatch(new GetAllLeaveTypes(userId));
      if (this.userService.getRole() === 'ADMIN') {
        this.store.dispatch(new GetAllRequests());
      }
    });
  }

  goBackToMenu() {
    this.showDashboard = false;
    this.showForm = false;
    this.showRequests = false;
    this.store.dispatch([ new SetLeaveRequests([]), new SetSelectedRequest(null) ]);
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
        this.store.dispatch(new GetAllRequests());
      } else if(this.userService.getRole() === 'MANAGER') {
        this.store.dispatch(new GetSubordinatesRequests());
      } else {
        this.getMyLeaveRequests();
      }
    }
    this.showRequests = !this.showRequests;
  }

  getMyLeaveRequests() {
    this.store.dispatch(new GetMyLeaveRequests());
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
      error: () => alert('Error while sending the request')
    });
  }

  selectRequestForEdit(request: any) {
    this.store.dispatch(new SetSelectedRequest({ ...request }));
  }

  get selectedRequestValue(): any | null {
    let selectedRequest: any | null = null;
    this.selectedRequest$.subscribe(request => {
      selectedRequest = request;
    }).unsubscribe();
    return selectedRequest;
  }

  updateLeaveRequest() {
    const selected = this.selectedRequestValue;
    if(!selected?.id) return;
    this.http.put(`/api/leaverequests/${selected.id}`, selected).subscribe({
      next: () => {
        alert('Request updated successfully!');
        this.getMyLeaveRequests();
        this.store.dispatch(new SetSelectedRequest(null));
      },
      error: () => alert('Error while updating the request')
    });
  }

  cancelEdit() {
    this.store.dispatch(new SetSelectedRequest(null));
  }

  deleteLeaveRequest(requestId: number) {
    if (confirm('Are you sure you want to delete this request?')) {
      this.http.delete(`/api/leaverequests/${requestId}/delete`).subscribe({
        next: () => {
          alert('Request deleted successfully!');
          this.getMyLeaveRequests();
        },
        error: () => alert('Error while deleting the request')
      });
    }
  }

  resetForm() {
    this.newLeaveRequest = { leaveTypeName: '', startDate: '', endDate: '', notes: '', status: 'PENDING' };
  }

  approveLeaveRequest(requestId: number) {
    const currentId = this.userService.getUser();
    this.http.put(`/api/leaverequests/${requestId}/approve?givenId=${currentId}`, {}).subscribe({
      next: () => {
        alert('Request approved successfully!');
        this.store.dispatch(new GetAllRequests());
      },
      error: () => alert('Error while approving the request')
    });
  }

  rejectLeaveRequest(requestId: number) {
    const currentId = this.userService.getUser();
    this.http.put(`/api/leaverequests/${requestId}/reject?givenId=${currentId}`, {}).subscribe({
      next: () => {
        alert('Request rejected successfully!');
        this.store.dispatch(new GetAllRequests());
      },
      error: () => alert('Error while rejecting the request')
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
          this.userService.getAllLeaveTypesForUser(user.id).subscribe(leaveTypes => {
            user.vacationBalance = leaveTypes["Vacation"] ?? 0;
            user.sickBalance = leaveTypes["Sick Leave"] ?? 0;
            user.unpaidBalance = leaveTypes["Unpaid"] ?? 0;
          });
        }
      });
    });
  }

  createUser() {
    this.http.post('/api/users/createUser', this.newUser).subscribe(() => {
      alert('User created successfully!');
      this.newUser = { name: '', email: '', role: '' };
      this.loadUsers();
    });
  }

  deleteUser(userId: number) {
    if (confirm('Are you sure you want to delete this user?')) {
      this.http.delete(`/api/users/deleteUser?id=${userId}`, { responseType: 'text' })
        .subscribe(() => {
          alert('User deleted successfully!');
          this.loadUsers();
        });
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
