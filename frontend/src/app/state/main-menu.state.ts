import { Action, Selector, State, StateContext } from "@ngxs/store";
import { SetAllRequests, SetLeaveRequests, SetSelectedRequest, SetSickLeave, SetUnpaidLeave, SetVacation } from "./main-menu.actions";

export interface MainMenuModel {
    leaveRequests: any[];
    allRequests: any[];
    selectedRequest: any | null;
    vacation: number | undefined;
    sickLeave: number | undefined;
    unpaid: number | undefined;
}
@State<MainMenuModel>({
  name: 'mainMenu',
    defaults: {
        leaveRequests: [],
        allRequests: [],
        selectedRequest: null,
        vacation: undefined,
        sickLeave: undefined,
        unpaid: undefined
    }
})
export class MainMenuState {
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
    static getVacation(state: MainMenuModel) {
        return state.vacation;
    }
    @Selector()
    static getSickLeave(state: MainMenuModel) {
        return state.sickLeave;
    }
    @Selector()
    static getUnpaidLeave(state: MainMenuModel) {
        return state.unpaid;
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
    @Action(SetVacation)
    setVacation(ctx: StateContext<MainMenuModel>, action: SetVacation) {
        ctx.patchState({vacation: action.vacation});
    }
    @Action(SetSickLeave)
    setSickLeave(ctx: StateContext<MainMenuModel>, action: SetSickLeave) {
        ctx.patchState({sickLeave: action.sickLeave});
    }
    @Action(SetUnpaidLeave)
    setUnpaidLeave(ctx: StateContext<MainMenuModel>, action: SetUnpaidLeave) {
        ctx.patchState({unpaid: action.unpaid});
    }
}