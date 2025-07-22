import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { UserService } from './user.service';

describe('UserService', () => {
  let service: UserService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule]
    });
    service = TestBed.inject(UserService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should correctly set and return the user ID', () => {
    service.setUserId(99);
    expect(service.getUser()).toBe(99);
  });

  it('should return a user via GET request', () => {
    const mockUser = { id: 5, name: 'Dania Test' };

    service.getUserById(5).subscribe(user => {
      expect(user).toEqual(mockUser);
    });

    const req = httpMock.expectOne('/api/users/5');
    expect(req.request.method).toBe('GET');
    req.flush(mockUser);
  });

  it('should load user data and set the role and name', () => {
    const mockUser = { id: 1, name: 'Test Name', role: 'admin' };

    service.loadUser().subscribe();

    const req = httpMock.expectOne('/api/users/1');
    req.flush(mockUser);

    expect(service.getRole()).toBe('ADMIN');
    expect(service.getName()).toBe('Test Name');
  });
});