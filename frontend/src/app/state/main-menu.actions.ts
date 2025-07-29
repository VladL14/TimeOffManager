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

export class SetVacation {
  static readonly type = '[MainMenu] SetVacation';
  constructor(public vacation: number | undefined) {}
}

export class SetSickLeave {
  static readonly type = '[MainMenu] SetSickLeave';
  constructor(public sickLeave: number | undefined) {}
}

export class SetUnpaidLeave {
  static readonly type = '[MainMenu] SetUnpaidLeave';
  constructor(public unpaid: number | undefined) {}
}
