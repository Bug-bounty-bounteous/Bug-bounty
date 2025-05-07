export interface User {
    id: number;
    name: string;
    email: string;
    userType: string;
  }
  
  export interface Developer extends User {
    username: string;
    rating: number;
    points: number;
  }
  
  export interface Company extends User {
    companyName: string;
  }
  