export interface LearningResource {
  id: number;
  title: string;
  description: string;
  url?: string;
  fileName?: string;
  filePath?: string;
  fileSize?: number;
  resourceType: string;
  date: string;
  reported: boolean;
  publisher: {
    id: number;
    companyName: string;
  };
  isFileResource?: boolean;
  resourceUrl?: string;
}

export interface ResourceRequest {
  title: string;
  description: string;
  url: string;
  resourceType: string;
}