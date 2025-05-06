import { Component } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { NgIf, NgForOf } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { UserService } from '../../user.service';

@Component({
  selector: 'app-user-dashboard',
  standalone: true,
  imports: [NgIf, NgForOf, FormsModule],
  templateUrl: './user-dashboard.component.html',
  styleUrls: ['./user-dashboard.component.scss']
})
export class UserDashboardComponent {
  leaveTypes: any[] = [];
  leaveRequests: any[] = [];

  newLeaveRequest = {
    leaveTypeId: '',
    startDate: '',
    endDate: '',
    notes: '',
    status: 'PENDING'
  };
  showForm = false;
  showRequests = false;

  

  toggleShowForm() {
    this.showForm = !this.showForm;
  }

  toggleShowRequests() {
    if (!this.showRequests) {
      this.loadDashboard();
    }
    this.showRequests = !this.showRequests;
  }


  selectedRequest: any = null;

  constructor(private http: HttpClient, private userService: UserService) {}

  ngOnInit() {
    this.getLeaveTypes();
  }
  loadDashboard() {
    this.getMyLeaveRequests();
  }

  getLeaveTypes() {
    this.http.get<any[]>('/api/leavetypes').subscribe(data => {
      this.leaveTypes = data;
    });
  }

  getMyLeaveRequests() {
    const currentUserId = this.userService.getUser();
    this.http.get<any[]>(`/api/leaverequests/user/${currentUserId}`).subscribe(data => {
      this.leaveRequests = data;
    });
  }

  submitNewRequest() {
    const currentUserId = this.userService.getUser();

    const requestData = {
      leaveTypeId: this.newLeaveRequest.leaveTypeId,
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
      leaveTypeId: request.leaveTypeId,
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

  resetForm() {
    this.newLeaveRequest = {
      leaveTypeId: '',
      startDate: '',
      endDate: '',
      notes: '',
      status: 'PENDING'
    };
  }
}
