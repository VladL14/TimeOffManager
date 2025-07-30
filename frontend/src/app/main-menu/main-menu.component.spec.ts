import { ComponentFixture, TestBed, fakeAsync, tick } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { of } from 'rxjs';
import { NgxsModule, Store } from '@ngxs/store';

import { MainMenuComponent } from './main-menu.component';
import { UserService } from '../user.service';
import { MainMenuState } from '../state/main-menu.state';
import { GetAllLeaveTypes, GetMyLeaveRequests, GetAllRequests, GetSubordinatesRequests } from '../state/main-menu.actions';

describe('MainMenuComponent', () => {
  let component: MainMenuComponent;
  let fixture: ComponentFixture<MainMenuComponent>;
  let store: Store;
  let httpMock: HttpTestingController;
  let mockUserService: any;

  const setupTestBed = (role: 'USER' | 'ADMIN' | 'MANAGER') => {
    mockUserService = {
      loadUser: () => of(null),
      getUser: () => 123,
      getName: () => 'Test User',
      getRole: () => role,
      getAllLeaveTypesForUser: () => of({ 'Vacation': 21, 'Sick Leave': 10 }),
      getUserById: (id: number) => of({ id: id, name: 'Some User' })
    };

    TestBed.configureTestingModule({
      imports: [
        MainMenuComponent,
        HttpClientTestingModule,
        NgxsModule.forRoot([MainMenuState])
      ],
      providers: [
        { provide: UserService, useValue: mockUserService }
      ]
    });

    fixture = TestBed.createComponent(MainMenuComponent);
    component = fixture.componentInstance;
    store = TestBed.inject(Store);
    httpMock = TestBed.inject(HttpTestingController);
  };

  afterEach(() => {
    httpMock.verify();
  });

  describe('with USER role', () => {
    beforeEach(() => setupTestBed('USER'));

    it('should create and dispatch GetAllLeaveTypes on init', fakeAsync(() => {
      spyOn(store, 'dispatch').and.callThrough();
      fixture.detectChanges();
      tick();
      expect(component).toBeTruthy();
      expect(store.dispatch).toHaveBeenCalledWith(new GetAllLeaveTypes(123));
    }));

    it('should dispatch GetMyLeaveRequests when toggleShowRequests is called', () => {
      spyOn(store, 'dispatch').and.callThrough();
      component.showRequests = false;

      component.toggleShowRequests();

      expect(store.dispatch).toHaveBeenCalledWith(new GetMyLeaveRequests());

      const req = httpMock.expectOne('/api/leaverequests/user/123');
      req.flush([]);
    });
  });

  describe('with ADMIN role', () => {
    beforeEach(() => setupTestBed('ADMIN'));

    it('should dispatch GetAllRequests on init', fakeAsync(() => {
      spyOn(store, 'dispatch').and.callThrough();

      fixture.detectChanges();
      tick();

      expect(store.dispatch).toHaveBeenCalledWith(new GetAllRequests());

      const req = httpMock.expectOne('/api/leaverequests');
      expect(req.request.method).toBe('GET');
      req.flush([]);
    }));

    it('should dispatch GetAllRequests when toggleShowRequests is called', () => {
      spyOn(store, 'dispatch').and.callThrough();
      component.showRequests = false;

      component.toggleShowRequests();

      expect(store.dispatch).toHaveBeenCalledWith(new GetAllRequests());

      const req = httpMock.expectOne('/api/leaverequests');
      req.flush([]);
    });

    it('should dispatch GetAllRequests after approving a request', () => {
      spyOn(store, 'dispatch').and.callThrough();

      component.approveLeaveRequest(1);

      const approveReq = httpMock.expectOne('/api/leaverequests/1/approve?givenId=123');
      approveReq.flush({});

      const refreshReq = httpMock.expectOne('/api/leaverequests');
      refreshReq.flush([]);

      expect(store.dispatch).toHaveBeenCalledWith(new GetAllRequests());
    });
  });

  describe('with MANAGER role', () => {
    beforeEach(() => setupTestBed('MANAGER'));

    it('should dispatch GetSubordinatesRequests when toggleShowRequests is called', () => {
      spyOn(store, 'dispatch').and.callThrough();
      component.showRequests = false;

      component.toggleShowRequests();

      expect(store.dispatch).toHaveBeenCalledWith(new GetSubordinatesRequests());

      const req = httpMock.expectOne('/api/leaverequests/viewSubordinatesLeaveRequests/123');
      req.flush([]);
    });
  });
});
