import { Component } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { NgIf, NgForOf } from '@angular/common';
import { RouterOutlet } from '@angular/router';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [NgIf, NgForOf],
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss']
})
export class AppComponent {
  users: any[] = [];
  projects: any[] = [];
  leaveTypes: any[] = [];
  leaveRequests: any[] = [];
  assignments: any[] = [];

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

  clearOthers(current: string) {
    if (current !== 'users') this.users = [];
    if (current !== 'projects') this.projects = [];
    if (current !== 'leaveTypes') this.leaveTypes = [];
    if (current !== 'leaveRequests') this.leaveRequests = [];
    if (current !== 'assignments') this.assignments = [];
  }
}
