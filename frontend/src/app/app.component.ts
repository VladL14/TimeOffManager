import { Component } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { NgIf, NgForOf } from '@angular/common';
import { FormsModule } from '@angular/forms';


@Component({
  selector: 'app-root',
  standalone: true,
  imports: [NgIf, NgForOf, FormsModule],
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss']
})
export class AppComponent {
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
    notes: ''
  };
  

  selectedRequest: any = null;

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
  userIdToSearch: number | null = null;  // Adăugăm un câmp pentru inputul din HTML

  searchLeaveRequestsByUser() {
    if (this.userIdToSearch !== null) {
      this.http.get<any[]>(`/api/leaverequests/user/${this.userIdToSearch}`).subscribe({
        next: (data) => {
          this.leaveRequests = data;
          this.clearOthers('leaveRequests');
        },
        error: (err) => {
          console.error('Eroare la căutarea cererilor după userId', err);
          alert('Nu s-au putut încărca cererile pentru acest utilizator.');
        }
      });
    }
  }
  
  createLeaveRequest() {
    this.http.post('/api/leaverequests', this.newLeaveRequest).subscribe({
      next: () => {
        alert('Cererea a fost adăugată cu succes!');
        this.loadLeaveRequests();
        this.newLeaveRequest = {
          userId: '',
          leaveTypeId: '',
          startDate: '',
          endDate: '',
          notes: ''
        };
      },
      error: () => {
        alert('Eroare la adăugarea cererii!');
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
  
    console.log('Updating request:', this.selectedRequest);  // VERIFICĂ că selectedRequest are id!
  
    this.http.put(`/api/leaverequests/${this.selectedRequest.id}`, this.selectedRequest).subscribe({
      next: () => {
        alert('Cererea a fost modificată cu succes!');
        this.loadLeaveRequests();
        this.selectedRequest = null;
      },
      error: (err) => {
        console.error('Eroare la modificarea cererii!', err);
        alert('Eroare la modificarea cererii!');
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