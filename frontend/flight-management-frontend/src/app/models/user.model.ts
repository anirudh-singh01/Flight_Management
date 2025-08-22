export interface User {
  userId?: number;
  userName: string;
  password: string;
  role?: UserRole;
  customerCategory?: CustomerCategory;
  phone: string;
  emailId: string;
  address1: string;
  address2?: string;
  city: string;
  state: string;
  zipCode: string;
  dob: Date;
  createdAt?: string;
}

export enum UserRole {
  CUSTOMER = 'CUSTOMER',
  ADMIN = 'ADMIN',
  STAFF = 'STAFF'
}

export enum CustomerCategory {
  REGULAR = 'REGULAR',
  SILVER = 'SILVER',
  GOLD = 'GOLD',
  PLATINUM = 'PLATINUM',
  PREMIUM = 'PREMIUM'
}

export interface ApiResponse {
  success: boolean;
  message: string;
  data: any;
}

export interface LoginRequest {
  userName: string;
  password: string;
}

export interface LoginResponse {
  userId: number;
  userName: string;
  role: UserRole;
  message: string;
}
