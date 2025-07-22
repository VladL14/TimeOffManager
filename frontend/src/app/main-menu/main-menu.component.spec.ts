import { ComponentFixture, TestBed, fakeAsync, tick } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { of } from 'rxjs';

import { MainMenuComponent } from './main-menu.component';
import { UserService } from '../user.service';
import { HttpClient } from '@angular/common/http';

describe('MainMenuComponent', () => {
  let component: MainMenuComponent;
  let fixture: ComponentFixture<MainMenuComponent>;
  let compiled: HTMLElement;
  let mockUserService: any;
  let httpMock: HttpTestingController;

  function setupTestBed(role: 'USER' | 'MANAGER' | 'ADMIN') {
    mockUserService = {
      loadUser: () => of(null), getVacationBalance: () => of(15),
      getSickBalance: () => of(10), getUnpaidBalance: () => of(5),
      getRole: () => role, getName: () => `Test ${role}`,
      getUser: () => (role === 'USER' ? 123 : role === 'MANAGER' ? 456 : 789),
      getUserById: (id: number) => of({ id, name: `User ${id}` })
    };
    TestBed.configureTestingModule({
      imports: [MainMenuComponent, HttpClientTestingModule],
      providers: [{ provide: UserService, useValue: mockUserService }]
    });
    fixture = TestBed.createComponent(MainMenuComponent);
    component = fixture.componentInstance;
    compiled = fixture.nativeElement;
    httpMock = TestBed.inject(HttpTestingController);
  }

  afterEach(() => {
    httpMock.verify();
  });

  it('should create the component', () => {
      setupTestBed('USER');
      expect(component).toBeTruthy();
    });

    it('should hide dashboard and forms on goBackToMenu()', () => {
      setupTestBed('USER');
      component.showDashboard = true;
      component.showForm = true;
      component.showRequests = true;
      component.goBackToMenu();
      expect(component.showDashboard).toBeFalse();
      expect(component.showForm).toBeFalse();
      expect(component.showRequests).toBeFalse();
    });

    it('should show the dashboard after clicking the goToDashboard() button', () => {
      setupTestBed('USER');
      component.goToDashboard();
      expect(component.showDashboard).toBeTrue();
    });

    it('should toggle showForm', () => {
      setupTestBed('USER');
      component.showForm = false;
      component.toggleShowForm();
      expect(component.showForm).toBeTrue();
    });

it('should load all requests and set usernames on loadAllRequests()', fakeAsync(() => {
    setupTestBed('ADMIN');
    const mockRequest = [{userId:1}];
    const mockUser = { name: 'Test'};
    const http = TestBed.inject(HttpClient) as HttpClient;
    spyOn(http, 'get').and.returnValue(of(mockRequest));
    spyOn(component.userService, 'getUserById').and.returnValue(of(mockUser));
    component.loadAllRequests();
    expect(component.allRequests[0].userName).toBe('Test');
  }));
it('should show user form when click toggleShowUserForm()', () => {
    setupTestBed('ADMIN');
    component.showUserForm = false;
    component.toggleShowUserForm();
    expect(component.showUserForm).toBeTrue();
  });

  it('should set selectedRequest on selectRequestForEdit()', () => {
    setupTestBed('USER');
    const mockRequest = { id: 1, leaveTypeName: 'Vacation', startDate: '2025-11-01', endDate: '2025-11-05', notes: 'Test', status: 'PENDING', userId: 2 };
    component.selectRequestForEdit(mockRequest);
    expect(component.selectedRequest).toEqual(mockRequest);
  });

  it('should clear selectedRequest on cancelEdit()', () => {
    setupTestBed('USER');
    component.selectedRequest = { id: 1, leaveTypeName: 'Vacation', startDate: '2025-11-01', endDate: '2025-11-05', notes: 'Test', status: 'PENDING', userId: 2 };
    component.cancelEdit();
    expect(component.selectedRequest).toBeNull();
  });




  describe('as a USER', () => {
    beforeEach(() => setupTestBed('USER'));

    it('should create and display balances after ngOnInit', fakeAsync(() => {
      fixture.detectChanges(); tick();
      component.showDashboard = true; fixture.detectChanges();
      expect(component).toBeTruthy(); expect(compiled.querySelector('#vacanta')?.textContent).toContain('15');
    }));

    it('should call getMyLeaveRequests on toggle for a USER', () => {
      const spy = spyOn(component, 'getMyLeaveRequests'); component.toggleShowRequests(); expect(spy).toHaveBeenCalled();
    });

    it('should display requests if they exist in component.leaveRequests', () => {
      component.showDashboard = true; component.showRequests = true;
      component.leaveRequests = [{ id: 1, leaveTypeName: 'Vacation', startDate: '2025-08-01', endDate: '2025-08-05' }];
      fixture.detectChanges(); const els = compiled.querySelectorAll('#viewrequestuser ul li'); expect(els.length).toBe(1);
    });

    it('should send a POST request on submitNewRequest', fakeAsync(() => {
      spyOn(window, 'alert'); spyOn(component, 'resetForm');
      component.newLeaveRequest = { leaveTypeName: 'Vacation', startDate: '2025-11-01', endDate: '2025-11-05', notes: 'Test', status: 'PENDING' };
      component.submitNewRequest();
      const reqPost = httpMock.expectOne('/api/leaverequests'); reqPost.flush({}); tick();
      const reqGet = httpMock.expectOne('/api/leaverequests/user/123'); reqGet.flush([]); tick();
      expect(window.alert).toHaveBeenCalledWith('The request was sent successfully!');
    }));

    it('should send a PUT request on updateLeaveRequest', fakeAsync(() => {
      spyOn(window, 'alert');
      component.selectedRequest = { id: 1, leaveTypeName: 'Vacation', startDate: '2025-11-01', endDate: '2025-11-05' };
      component.updateLeaveRequest();
      const reqPut = httpMock.expectOne('/api/leaverequests/1'); reqPut.flush({}); tick();
      const reqGet = httpMock.expectOne('/api/leaverequests/user/123'); reqGet.flush([]); tick();
      expect(window.alert).toHaveBeenCalledWith('Request updated successfully!');
    }));

    it('should send a DELETE request on deleteLeaveRequest', fakeAsync(() => {
      spyOn(window, 'alert'); spyOn(window, 'confirm').and.returnValue(true);
      component.deleteLeaveRequest(1);
      const reqDelete = httpMock.expectOne('/api/leaverequests/1/delete'); reqDelete.flush({}); tick();
      const reqGet = httpMock.expectOne('/api/leaverequests/user/123'); reqGet.flush([]); tick();
      expect(window.alert).toHaveBeenCalledWith('Request deleted successfully!');
    }));
  });

  describe('as a MANAGER', () => {
    beforeEach(() => setupTestBed('MANAGER'));

    it('should call loadSubordinatesRequests on toggle', () => {
      const spy = spyOn(component, 'loadSubordinatesRequests'); component.toggleShowRequests(); expect(spy).toHaveBeenCalled();
    });

    it('should send a PUT request on approveLeaveRequest', fakeAsync(() => {
      spyOn(window, 'alert'); const spyLoad = spyOn(component, 'loadAllRequests').and.callFake(() => {});
      component.approveLeaveRequest(99);
      const req = httpMock.expectOne('/api/leaverequests/99/approve?givenId=456'); req.flush({}); tick();
      expect(window.alert).toHaveBeenCalledWith('Request approved successfully!');
    }));

    it('should send a PUT request on rejectLeaveRequest', fakeAsync(() => {
      spyOn(window, 'alert'); const spyLoad = spyOn(component, 'loadAllRequests').and.callFake(() => {});
      component.rejectLeaveRequest(99);
      const req = httpMock.expectOne('/api/leaverequests/99/reject?givenId=456'); req.flush({}); tick();
      expect(window.alert).toHaveBeenCalledWith('Request rejected successfully!');
    }));
  });

  describe('as an ADMIN', () => {
    beforeEach(fakeAsync(() => {
      setupTestBed('ADMIN');
      fixture.detectChanges();
      const req = httpMock.expectOne('/api/leaverequests');
      req.flush([]);
      tick();
    }));

    it('should call loadAllRequests on toggle', () => {
      const spy = spyOn(component, 'loadAllRequests'); component.toggleShowRequests(); expect(spy).toHaveBeenCalled();
    });

    it('should call loadUsers on toggleShowUsers', () => {
      const spy = spyOn(component, 'loadUsers'); component.toggleShowUsers(); expect(spy).toHaveBeenCalled();
    });

    it('should display users if they exist', () => {
      component.showDashboard = true; component.showUsers = true;
      component.users = [{ id: 1, name: 'User unu', role: 'USER', isActive: true }];
      fixture.detectChanges(); const els = compiled.querySelectorAll('#viewusers ul li'); expect(els.length).toBe(1);
    });

    it('should send a POST request on createUser', fakeAsync(() => {
      spyOn(window, 'alert'); const spyLoad = spyOn(component, 'loadUsers').and.callFake(() => {});
      component.newUser = { name: 'New Guy', email: 'new@guy.com', role: 'USER'};
      component.createUser();
      const req = httpMock.expectOne('/api/users/createUser'); req.flush({}); tick();
      expect(window.alert).toHaveBeenCalledWith('User created successfully!');
    }));

    it('should send a DELETE request on deleteUser', fakeAsync(() => {
      spyOn(window, 'alert'); spyOn(window, 'confirm').and.returnValue(true); const spyLoad = spyOn(component, 'loadUsers').and.callFake(() => {});
      component.deleteUser(99);
      const req = httpMock.expectOne('/api/users/deleteUser?id=99'); req.flush('OK'); tick();
      expect(window.alert).toHaveBeenCalledWith('User deleted successfully!');
    }));

    it('should send 3 PUT requests on updateBalances', fakeAsync(() => {
      spyOn(window, 'alert'); const spyLoad = spyOn(component, 'loadUsers').and.callFake(() => {});
      component.updateBalances(123, 20, 10, 5);
      const reqV = httpMock.expectOne('/api/leavetypes/user/123/vacation/balance?newBalance=20');
      const reqS = httpMock.expectOne('/api/leavetypes/user/123/sick_leave/balance?newBalance=10');
      const reqU = httpMock.expectOne('/api/leavetypes/user/123/unpaid/balance?newBalance=5');
      reqV.flush({}); reqS.flush({}); reqU.flush({}); tick();
      expect(window.alert).toHaveBeenCalledWith('Balances updated successfully!');
    }));
  });
});
