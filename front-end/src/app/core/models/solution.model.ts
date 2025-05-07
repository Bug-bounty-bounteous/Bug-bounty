export interface Solution {
    id: number;
    description: string;
    codeLink: string;
    status: string;
    submittedAt: string;
    reviewedAt?: string;
    developer: any;
    bug: any;
    feedbacks: any[];
  }
  