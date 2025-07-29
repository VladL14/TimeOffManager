import { ComponentFixture, TestBed, fakeAsync, tick } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { of } from 'rxjs';
import { NgxsModule, Store } from '@ngxs/store';
import { Injectable } from '@angular/core';

import { MainMenuComponent } from './main-menu.component';
import { UserService } from '../user.service';
import { MainMenuState } from '../state/main-menu.state';
import { SetAllRequests, SetLeaveRequests, SetSelectedRequest, SetVacation, SetSickLeave, SetUnpaidLeave } from '../state/main-menu.actions';

describe('MainMenuComponent', () => {
  let component: MainMenuComponent;
  let fixture: ComponentFixture<MainMenuComponent>;
  let compiled: HTMLElement;
  let mockUserService: any;
  let httpMock: HttpTestingController;
  let store: Store;

  function setupTestBed(role: 'USER' | 'MANAGER' | 'ADMIN') {
    mockUserService = {
      loadUser: () => of(null),
      getVacationBalance: (id: number) => of(15),
      getSickBalance: (id: number) => of(10),
      getUnpaidBalance: (id: number) => of(5),
      getRole: () => role,
      getName: () => `Test ${role}`,
      getUser: () => (role === 'USER' ? 123 : role === 'MANAGER' ? 456 : 789),
      getUserById: (id: number) => of({ id: id, name: `User ${id}` })
    };

    TestBed.configureTestingModule({
      imports: [MainMenuComponent, HttpClientTestingModule, NgxsModule.forRoot([MainMenuState])],
      providers: [{ provide: UserService, useValue: mockUserService }]
    });

    fixture = TestBed.createComponent(MainMenuComponent);
    component = fixture.componentInstance;
    compiled = fixture.nativeElement;
    httpMock = TestBed.inject(HttpTestingController);
    store = TestBed.inject(Store);
  }

  afterEach(() => {
    httpMock.verify();
  });

  it('should toggle showForm', () => {
    setupTestBed('USER');
    component.showForm = false;
    component.toggleShowForm();
    expect(component.showForm).toBeTrue();
  });

  it('should dispatch SetSelectedRequest on selectRequestForEdit', () => {
    setupTestBed('USER');
    const dispatchSpy = spyOn(store, 'dispatch');
    const mockRequest = { id: 1, leaveTypeName: 'Vacation' };
    component.selectRequestForEdit(mockRequest);

    expect(dispatchSpy).toHaveBeenCalledWith(jasmine.objectContaining({
      request: jasmine.objectContaining({ id: 1 })
    }));
  });

  it('should dispatch CancelEdit on cancelEdit', () => {
    setupTestBed('USER');
    const dispatchSpy = spyOn(store, 'dispatch');
    component.cancelEdit();
    expect(dispatchSpy).toHaveBeenCalledWith(new SetSelectedRequest(null));
  });

  it('should dispatch actions after ngOnInit completes', fakeAsync(() => {
    setupTestBed('USER');
    const dispatchSpy = spyOn(store, 'dispatch');

    fixture.detectChanges();
    tick();
    tick();
    tick();
    tick();

    expect(dispatchSpy).toHaveBeenCalledWith(new SetVacation(15));
    expect(dispatchSpy).toHaveBeenCalledWith(new SetSickLeave(10));
    expect(dispatchSpy).toHaveBeenCalledWith(new SetUnpaidLeave(5));
  }));

  it('should send a POST request on submitNewRequest', fakeAsync(() => {
    setupTestBed('USER');
    spyOn(window, 'alert');
    spyOn(component, 'resetForm');
    spyOn(component, 'getMyLeaveRequests').and.callFake(() => {});

    component.newLeaveRequest = { leaveTypeName: 'Vacation', startDate: '2025-11-01', endDate: '2025-11-05', notes: 'Test', status: 'PENDING' };
    component.submitNewRequest();

    const reqPost = httpMock.expectOne('/api/leaverequests');
    reqPost.flush({});
    tick();

    expect(window.alert).toHaveBeenCalledWith('The request was sent successfully!');
    expect(component.getMyLeaveRequests).toHaveBeenCalled();
  }));

  it('should display requests from the store', () => {
    setupTestBed('USER');
    store.reset({
      mainMenu: {
        leaveRequests: [{ id: 1, leaveTypeName: 'Vacation', startDate: '2025-08-01', endDate: '2025-08-05' }]
      }
    });

    component.showDashboard = true;
    component.showRequests = true;
    fixture.detectChanges();

    const requestElements = compiled.querySelectorAll('#viewrequestuser ul li');
    expect(requestElements.length).toBe(1);
    expect(requestElements[0].textContent).toContain('Vacation');
  });
});