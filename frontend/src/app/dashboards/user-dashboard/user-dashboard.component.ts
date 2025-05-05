import { Component } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { NgIf, NgForOf } from '@angular/common';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-user-dashboard',
  standalone: true,
  imports: [NgIf, NgForOf, FormsModule],
  templateUrl: './user-dashboard.component.html',
  styleUrls: ['./user-dashboard.component.scss']
})
export class UserDashboardComponent {
  users: any[] = [];
  projects: any[] = [];
  leaveTypes: any[] = [];
  leaveRequests: any[] = [];
  assignments: any[] = [];

  newLeaveRequest = {
    userId: '',
    leaveTypeId: '',
    startDate: '',
    endDate: '',
    notes: '',
    status: 'PENDING'
  };

  selectedRequest: any = null;
  userIdToSearch: number | null = null;

  constructor(private http: HttpClient) {}

  loadUsers() {
    this.http.get<any[]>('/api/users').subscribe(data => {
      this.users = data;
      this.clearOthers('users');
    });
  }

  loadProjects() {
    this.http.get<any[]>('/api/projects').subscribe(data => {
      this.projects = data;
      this.clearOthers('projects');
    });
  }

  loadLeaveTypes() {
    this.http.get<any[]>('/api/leavetypes').subscribe(data => {
      this.leaveTypes = data;
      this.clearOthers('leaveTypes');
    });
  }

  loadLeaveRequests() {
    this.http.get<any[]>('/api/leaverequests').subscribe(data => {
      this.leaveRequests = data;
      this.clearOthers('leaveRequests');
    });
  }

  loadAssignments() {
    this.http.get<any[]>('/api/assignments').subscribe(data => {
      this.assignments = data;
      this.clearOthers('assignments');
    });
  }

  searchLeaveRequestsByUser() {
    if (this.userIdToSearch !== null) {
      this.http.get<any[]>(`/api/leaverequests/user/${this.userIdToSearch}`).subscribe({
        next: (data) => {
          this.leaveRequests = data;
          this.clearOthers('leaveRequests');
        },
        error: (err) => {
          console.error('Cannot find by userId', err);
          alert('The requests could not be loaded');
        }
      });
    }
  }

  createLeaveRequest() {
    this.http.post('/api/leaverequests', this.newLeaveRequest).subscribe({
      next: () => {
        alert('The request was added successfully!');
        this.loadLeaveRequests();
        this.newLeaveRequest = {
          userId: '',
          leaveTypeId: '',
          startDate: '',
          endDate: '',
          notes: '',
          status: 'PENDING'
        };
      },
      error: () => {
        alert('Error adding the request!');
      }
    });
  }

  selectRequestForEdit(request: any) {
    this.selectedRequest = { ...request };
    console.log('Selected for edit:', this.selectedRequest);
  }

  updateLeaveRequest() {
    if (!this.selectedRequest?.id) {
      console.error('ID is missing for update');
      return;
    }

    console.log('Updating request:', this.selectedRequest);

    this.http.put(`/api/leaverequests/${this.selectedRequest.id}`, this.selectedRequest).subscribe({
      next: () => {
        alert('The request was updated successfully!');
        this.loadLeaveRequests();
        this.selectedRequest = null;
      },
      error: (err) => {
        console.error('Error at the modify of request', err);
        alert('Error at the modify of request');
      }
    });
  }

  cancelEdit() {
    this.selectedRequest = null;
  }

  clearOthers(current: string) {
    if (current !== 'users') this.users = [];
    if (current !== 'projects') this.projects = [];
    if (current !== 'leaveTypes') this.leaveTypes = [];
    if (current !== 'leaveRequests') this.leaveRequests = [];
    if (current !== 'assignments') this.assignments = [];
  }
}
