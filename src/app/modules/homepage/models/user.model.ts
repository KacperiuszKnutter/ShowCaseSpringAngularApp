export interface UserModel {
  email : string;
  password: string;

}

export interface GetUsersResponse {
  email : string;
  password: string;
}

export class User {
  constructor(public email: string , public username: string) {
  }
}
