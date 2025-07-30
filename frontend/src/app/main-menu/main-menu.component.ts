import { Component } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { UserService} from '../user.service';
import { AsyncPipe, NgIf, NgForOf } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { Store } from '@ngxs/store';
import { MainMenuState } from '../state/main-menu.state';

import { firstValueFrom, map, Observable } from 'rxjs';
import { ApproveLeaveRequest, GetAllLeaveTypes, GetAllRequests, GetMyLeaveRequests, GetSubordinatesRequests, LoadCurrentUser, LoadUsers, RejectLeaveRequest, SetAllRequests, SetLeaveRequests, SetSelectedRequest, SubmitLeaveRequest, UpdateUserBalances} from '../state/main-menu.actions';


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
  users$: Observable<any[]>;
  userError$: Observable<string | null>;
  currentUser$: Observable<any | null>;
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

    this.users$ = this.store.select(MainMenuState.getUsers);
    this.userError$ = this.store.select(MainMenuState.getUserError);
    this.currentUser$ = this.store.select(MainMenuState.getCurrentUser);


  }
  ngOnInit() {
    this.store.dispatch(new LoadCurrentUser());

    this.currentUser$.subscribe(user => {
      if (user) {
        this.store.dispatch(new GetAllLeaveTypes(user.id));
        if (user.role === 'ADMIN') {
          this.store.dispatch(new GetAllRequests());
          this.store.dispatch(new LoadUsers());
        }
      }
    });
    this.users$ = this.store.select(MainMenuState.getUsers).pipe(
      map(users => [...users].sort((a, b) => a.name.localeCompare(b.name)))
    );
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

    this.store.dispatch(new LoadCurrentUser());
      this.currentUser$.subscribe(user => {
      if(user.role === 'ADMIN') {
        this.store.dispatch(new GetAllRequests());
      } else if(user.role === 'MANAGER') {
        this.store.dispatch(new GetSubordinatesRequests());
      } else {
        this.store.dispatch(new GetMyLeaveRequests());
      }
    }
      );

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


  async submitNewRequest() {
    const user = await firstValueFrom(this.currentUser$);

    const newreq = {

      leaveTypeName: this.newLeaveRequest.leaveTypeName,
      startDate: this.newLeaveRequest.startDate,
      endDate: this.newLeaveRequest.endDate,
      notes: this.newLeaveRequest.notes,
      userId: user.id
    };

    this.store.dispatch(new SubmitLeaveRequest(newreq));
    this.resetForm();

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
        this.store.dispatch(new GetMyLeaveRequests());
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
          this.store.dispatch(new GetMyLeaveRequests());
        },
        error: () => alert('Error while deleting the request')
      });
    }
  }


  resetForm() {
    this.newLeaveRequest = { leaveTypeName: '', startDate: '', endDate: '', notes: '', status: 'PENDING' };
  }


  async approveLeaveRequest(requestId: number) {
  const user = await firstValueFrom(this.currentUser$);
  this.store.dispatch(new ApproveLeaveRequest(requestId, user.id));
}

  async rejectLeaveRequest(requestId: number) {
    const user = await firstValueFrom(this.currentUser$);
    this.store.dispatch(new RejectLeaveRequest(requestId, user.id));
  }

  toggleShowUsers() {
    if (!this.showUsers) {
      this.store.dispatch(new LoadUsers());
    }
    this.showUsers = !this.showUsers;
  }

  toggleShowUserForm() {
    this.showUserForm = !this.showUserForm;
  }


  createUser() {
    this.http.post('/api/users/createUser', this.newUser).subscribe(() => {
      alert('User created successfully!');
      this.newUser = { name: '', email: '', role: '' };

      this.store.dispatch(new LoadUsers());
    });
  }

  deleteUser(userId: number) {
    if (confirm('Are you sure you want to delete this user?')) {
      this.http.delete(`/api/users/deleteUser?id=${userId}`, { responseType: 'text' })
        .subscribe(() => {
          alert('User deleted successfully!');

          this.store.dispatch(new LoadUsers());
        });
    }
  }

  updateBalances(userId: number, vacation: number, sickLeave: number, unpaid: number) {
  this.store.dispatch(new UpdateUserBalances(userId, vacation, sickLeave, unpaid));
}




}

