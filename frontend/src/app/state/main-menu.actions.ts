export class SetLeaveRequests {
  static readonly type = '[MainMenu] SetLeaveRequests';
  constructor(public requests: any[]) {}
}

export class SetSelectedRequest {
  static readonly type = '[MainMenu] SetSelectedRequest';
  constructor(public request: any | null) {}
}

export class SetAllRequests {
  static readonly type = '[MainMenu] SetAllRequests';
  constructor(public requests: any[]) {}
}

export class GetAllLeaveTypes {
  static readonly type = '[MainMenu] GetAllLeaveTypes';
  constructor(public userId: number) {}
}
export class LoadUsers {
  static readonly type = '[MainMenu] Load Users';
}

export class LoadUsersSuccess {
  static readonly type = '[MainMenu] Load Users Success';
  constructor(public users: any[]) {}
}

export class LoadUsersFail {
  static readonly type = '[MainMenu] Load Users Fail';
  constructor(public error: string) {}
}
export class LoadCurrentUser {
  static readonly type = '[MainMenu] Load Current User';
}
export class SetCurrentUser {
  static readonly type = '[MainMenu] Set Current User';
  constructor(public user: any) {}
}
export class GetMyLeaveRequests {
  static readonly type = '[MainMenu] Get My Leave Requests';
}

export class GetAllRequests {
  static readonly type = '[MainMenu] Get All Requests';
}

export class GetSubordinatesRequests {
  static readonly type = '[MainMenu] Get Subordinates Requests';
}

export class UpdateUserBalances {
  static readonly type = '[Users] Update User Balances';
  constructor(
    public userId: number,
    public vacation: number,
    public sick: number,
    public unpaid: number
  ) {}

}
export class ApproveLeaveRequest {
  static readonly type = '[Leave] Approve Request';
  constructor(public requestId: number, public approverId: number) {}
}

export class RejectLeaveRequest {
  static readonly type = '[Leave] Reject Request';
  constructor(public requestId: number, public approverId: number) {}
}

export class SubmitLeaveRequest {
  static readonly type = '[Leave] Submit New Request';
  constructor(public newreq: {
    leaveTypeName: string;
    startDate: string;
    endDate: string;
    notes: string;
    userId: number;
  }) {}
}






