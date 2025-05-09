export interface User {
    id?: number;
    username?: string;
    firstName: string;
    lastName: string;
    email: string;
    role?: string;
    rating?: number;
    points?: number;
  }
  
  export interface AuthResponse {
    token: string;
    type: string;
    id: number;
    username: string;
    email: string;
    role: string;
  }
  
  export interface LoginRequest {
    email: string;
    password: string;
    rememberMe?: boolean;
  }
  
  export interface RegisterRequest {
    firstName: string;
    lastName: string;
    email: string;
    username: string;
    password: string;
    role: string;
  }
  