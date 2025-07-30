import { Action, Selector, State, StateContext } from "@ngxs/store";
import { SetAllRequests, SetLeaveRequests, SetSelectedRequest, GetAllLeaveTypes, GetMyLeaveRequests, GetAllRequests, GetSubordinatesRequests } from "./main-menu.actions";
import { UserService } from "../user.service";
import { Injectable } from "@angular/core";
import { tap, switchMap, map, of, forkJoin } from "rxjs";
import { HttpClient } from "@angular/common/http";

export interface MainMenuModel {
  leaveRequests: any[];
  allRequests: any[];
  selectedRequest: any | null;
  leaveBalances: { [key: string]: number };
}

@State<MainMenuModel>({
  name: 'mainMenu',
  defaults: {
    leaveRequests: [],
    allRequests: [],
    selectedRequest: null,
    leaveBalances: {}
  }
})
@Injectable()
export class MainMenuState {
  constructor(private userService: UserService, private http: HttpClient) {}

  @Selector() static getLeaveRequests(state: MainMenuModel) { return state.leaveRequests; }
  @Selector() static getSelectedRequest(state: MainMenuModel) { return state.selectedRequest; }
  @Selector() static getAllRequests(state: MainMenuModel) { return state.allRequests; }
  @Selector() static getLeaveBalances(state: MainMenuModel) { return state.leaveBalances; }

  @Action(SetLeaveRequests) setLeaveRequests(ctx: StateContext<MainMenuModel>, action: SetLeaveRequests) { ctx.patchState({leaveRequests: action.requests}); }
  @Action(SetSelectedRequest) setSelectedRequest(ctx: StateContext<MainMenuModel>, action: SetSelectedRequest) { ctx.patchState({selectedRequest: action.request}); }
  @Action(SetAllRequests) setAllRequests(ctx: StateContext<MainMenuModel>, action: SetAllRequests) { ctx.patchState({allRequests: action.requests}); }

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
}
