import { Action, Selector, State, StateContext } from "@ngxs/store";
import { SetAllRequests, SetLeaveRequests, SetSelectedRequest } from "./main-menu.actions";

export interface MainMenuModel {
    leaveRequests: any[];
    allRequests: any[];
    selectedRequest: any | null;
}
@State<MainMenuModel>({
  name: 'mainMenu',
    defaults: {
        leaveRequests: [],
        allRequests: [],
        selectedRequest: null
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
}