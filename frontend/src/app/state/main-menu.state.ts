import { Action, Selector, State, StateContext } from "@ngxs/store";

import { SetAllRequests, SetLeaveRequests, SetSelectedRequest, GetAllLeaveTypes, LoadUsers, LoadUsersSuccess, LoadUsersFail, LoadCurrentUser, SetCurrentUser, GetMyLeaveRequests, GetAllRequests, GetSubordinatesRequests, UpdateUserBalances, ApproveLeaveRequest, RejectLeaveRequest, SubmitLeaveRequest } from "./main-menu.actions";
import { UserService } from "../user.service";
import { Injectable } from "@angular/core";
import { catchError, forkJoin, map, of, switchMap, tap } from "rxjs";
import { HttpClient } from "@angular/common/http";

export interface MainMenuModel {
    leaveRequests: any[];
    allRequests: any[];
    selectedRequest: any | null;
    leaveBalances: { [key: string]: number };
    users: any[];
    userError: string | null;
    currentUser: any | null;

}

@State<MainMenuModel>({
  name: 'mainMenu',
    defaults: {
        leaveRequests: [],
        allRequests: [],
        selectedRequest: null,
        leaveBalances: {},
        users: [],
        userError: null,
        currentUser: null
    }
})
@Injectable()
export class MainMenuState {
    constructor(private userService: UserService, private http: HttpClient) {}
    @Selector()
    static getLeaveRequests(state: MainMenuModel) {
        return state.leaveRequests;
    }
    @Selector()
    static getSelectedRequest(state: MainMenuModel) {
        return state.selectedRequest;
    }
    @Selector()
    static getAllRequests(state: MainMenuModel) {
        return state.allRequests;
    }
    @Selector()
    static getLeaveBalances(state: MainMenuModel) {
        return state.leaveBalances;
    }
    @Selector()
    static getUsers(state: MainMenuModel) {
        return state.users;
    }
    @Selector()
    static getUserError(state: MainMenuModel) {
        return state.userError;
    }
    @Selector()
    static getCurrentUser(state: MainMenuModel | undefined) {
    return state?.currentUser ?? null;
    }


    @Action(SetLeaveRequests)
    setLeaveRequests(ctx: StateContext<MainMenuModel>, action: SetLeaveRequests) {
        ctx.patchState({leaveRequests: action.requests});
    }
    @Action(SetSelectedRequest)
    setSelectedRequest(ctx: StateContext<MainMenuModel>, action: SetSelectedRequest) {
        ctx.patchState({selectedRequest: action.request});
    }
    @Action(SetAllRequests)
    setAllRequests(ctx: StateContext<MainMenuModel>, action: SetAllRequests) {
        ctx.patchState({allRequests: action.requests});
    }
    
    @Action(GetAllLeaveTypes)
    getAllLeaveTypes(ctx: StateContext<MainMenuModel>, action: GetAllLeaveTypes) {

    return this.userService.getAllLeaveTypesForUser(action.userId).pipe(
      tap((res: { [key: string]: number }) => {
        ctx.patchState({ leaveBalances: res });
      })
    );
  }

  @Action(GetMyLeaveRequests)
  getMyLeaveRequests(ctx: StateContext<MainMenuModel>) {
    const userId = this.userService.getUser();
    return this.http.get<any[]>(`/api/leaverequests/user/${userId}`).pipe(
      tap(requests => {
        ctx.dispatch(new SetLeaveRequests(requests));
      })
    );
    }
    @Action(LoadUsers)
    loadUsers(ctx: StateContext<MainMenuModel>) {
    return this.userService.getAllUsers().pipe(
        tap(users => {
        const activeUsers = users.filter(user => user.isActive);
        const updatedUsers: any[] = [];
        let loadedCount = 0;

        activeUsers.forEach(user => {
            this.userService.getAllLeaveTypesForUser(user.id).subscribe(balances => {
            user.vacationBalance = balances["Vacation"] ?? 0;
            user.sickBalance = balances["Sick Leave"] ?? 0;
            user.unpaidBalance = balances["Unpaid"] ?? 0;
            updatedUsers.push(user);
            loadedCount++;

            if (loadedCount === activeUsers.length) {
                ctx.dispatch(new LoadUsersSuccess(updatedUsers));
            }
            });
        });
        }),
        catchError(() => {
        return of(ctx.dispatch(new LoadUsersFail('Could not load users')));
        })
    );
    }

    @Action(LoadUsersSuccess)
    loadUsersSuccess(ctx: StateContext<MainMenuModel>, action: LoadUsersSuccess) {
    ctx.patchState({
        users: action.users,
        userError: null
    });
    }

    @Action(LoadUsersFail)
    loadUsersFail(ctx: StateContext<MainMenuModel>, action: LoadUsersFail) {
    ctx.patchState({
        users: [],
        userError: action.error
    });
    }

    @Action(LoadCurrentUser)
    loadCurrentUser(ctx: StateContext<MainMenuModel>) {
    return this.userService.loadUser().pipe(
        tap(() => {
        const user = {
            id: this.userService.getUser(),
            name: this.userService.getName(),
            role: this.userService.getRole()
        };
        ctx.dispatch(new SetCurrentUser(user));
        })
    );
    }

    @Action(SetCurrentUser)
    setCurrentUser(ctx: StateContext<MainMenuModel>, action: SetCurrentUser) {
    ctx.patchState({ currentUser: action.user });
    }


    @Action(GetMyLeaveRequests)
    getMyLeaveRequests(ctx: StateContext<MainMenuModel>) {
        const userId = this.userService.getUser();
        return this.http.get<any[]>(`/api/leaverequests/user/${userId}`).pipe(
        tap(requests => {
            ctx.dispatch(new SetLeaveRequests(requests));
        })
        );
    }

    @Action(GetAllRequests)
    getAllRequests(ctx: StateContext<MainMenuModel>) {
        return this.http.get<any[]>('/api/leaverequests').pipe(
        switchMap(requests => {
            if (requests.length === 0) return of([]);
            const requestsWithDetails = requests.map(request =>
            this.userService.getUserById(request.userId).pipe(
                map(user => ({ ...request, userName: user.name }))
            )
            );
            return forkJoin(requestsWithDetails);
        }),
        tap(completeRequests => {
            ctx.dispatch(new SetAllRequests(completeRequests));
        })
        );
    }

    @Action(GetSubordinatesRequests)
    getSubordinatesRequests(ctx: StateContext<MainMenuModel>) {
        const managerId = this.userService.getUser();
        return this.http.get<any[]>(`/api/leaverequests/viewSubordinatesLeaveRequests/${managerId}`).pipe(
        switchMap(requests => {
            if (requests.length === 0) {
            return of([]);
            }

            const requestsWithDetails = requests.map(request =>
            forkJoin({
                user: this.userService.getUserById(request.userId),
                projects: this.http.get<any[]>(`/api/projects/user/${request.userId}`).pipe(
                switchMap(projects => {
                    if (projects.length === 0) return of([]);
                    const projectsWithMembers = projects.map(project =>
                    this.http.get<any[]>(`/api/projects/${project.id}/users`).pipe(
                        map(members => ({ ...project, members: members }))
                    )
                    );
                    return forkJoin(projectsWithMembers);
                })
                )
            }).pipe(
                map(({ user, projects }) => ({
                ...request,
                userName: user.name,
                projects: projects
                }))
            )
            );
            return forkJoin(requestsWithDetails);
        }),
        tap(completeRequests => {
            ctx.dispatch(new SetAllRequests(completeRequests));
        })
        );
    }
    @Action(UpdateUserBalances)
    updateUserBalances(ctx: StateContext<MainMenuModel>, action: UpdateUserBalances) {
    const { userId, vacation, sick, unpaid } = action;

    const vacation$ = this.http.put(`/api/leavetypes/user/${userId}/vacation/balance?newBalance=${vacation}`, {});
    const sick$ = this.http.put(`/api/leavetypes/user/${userId}/sick_leave/balance?newBalance=${sick}`, {});
    const unpaid$ = this.http.put(`/api/leavetypes/user/${userId}/unpaid/balance?newBalance=${unpaid}`, {});

    return forkJoin([vacation$, sick$, unpaid$]).pipe(
        tap(() => {
        alert('Balances updated successfully!');
        ctx.dispatch(new LoadUsers());
        }),
        catchError(error => {
        alert('Error while updating balances');
        return of(error);
        })
    );
}

@Action(ApproveLeaveRequest)
approveLeaveRequest(ctx: StateContext<MainMenuModel>, action: ApproveLeaveRequest) {
  const { requestId, approverId } = action;
  return this.http.put(`/api/leaverequests/${requestId}/approve?givenId=${approverId}`, {}).pipe(
    tap(() => {
      alert('Request approved successfully!');
      ctx.dispatch(new GetAllRequests());
    }),
    catchError(error => {
      alert('Error while approving the request');
      return of(error);
    })
  );
}

@Action(RejectLeaveRequest)
rejectLeaveRequest(ctx: StateContext<MainMenuModel>, action: RejectLeaveRequest) {
  const { requestId, approverId } = action;
  return this.http.put(`/api/leaverequests/${requestId}/reject?givenId=${approverId}`, {}).pipe(
    tap(() => {
      alert('Request rejected successfully!');
      ctx.dispatch(new GetAllRequests());
    }),
    catchError(error => {
      alert('Error while rejecting the request');
      return of(error);
    })
  );
}

@Action(SubmitLeaveRequest)
submitLeaveRequest(ctx: StateContext<MainMenuModel>, action: SubmitLeaveRequest) {
  const data = { ...action.newreq, status: 'PENDING' };

  return this.http.post('/api/leaverequests', data).pipe(
    tap(() => {
      alert('The request was sent successfully!');
      ctx.dispatch(new GetMyLeaveRequests());
    }),
    catchError(error => {
      alert('Error while sending the request');
      return of(error);
    })
  );
}


  }
