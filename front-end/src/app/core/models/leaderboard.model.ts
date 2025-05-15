import { Bug } from './bug.model';

export interface Leaderboard {
  developers: DeveloperInfo[];
  myRank: number;
  myRating: number;
  myPoints: number;
  length: number;
  firstName: string;
  lastName: string;
}

export interface DeveloperInfo {
  id: number;
  firstName: string;
  lastName: string;
  email: string;
  points: number;
  rating: number;
  claimedBugs: Bug[];
}
