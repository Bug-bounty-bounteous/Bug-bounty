export interface LearningResource {
  id: number;
  title: string;
  description: string;
  url: string;
  resourceType: string;
  date: string;
  reported: boolean;
  publisher: {
    id: number;
    companyName: string;
  };
}

export interface ResourceRequest {
  title: string;
  description: string;
  url: string;
  resourceType: string;
}