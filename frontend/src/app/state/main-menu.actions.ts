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
