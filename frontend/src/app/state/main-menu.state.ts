import { Action, Selector, State, StateContext } from "@ngxs/store";
import { SetAllRequests, SetLeaveRequests, SetSelectedRequest, GetAllLeaveTypes } from "./main-menu.actions";
import { UserService } from "../user.service";
import { Injectable } from "@angular/core";
import { tap } from "rxjs";

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
    constructor(private userService: UserService) {}
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
        ctx.patchState({
            leaveBalances: res
        });
        })
    );
    }

    }