import { Component } from '@angular/core';
import { SidebarLayoutComponent } from '../../layout/sidebar-layout/sidebar-layout.component';
import { CommonModule } from '@angular/common';
import { SearchBarComponent } from '../../shared/components/search-bar/search-bar.component';
import { FormsModule } from '@angular/forms';
import { PaginationComponent } from '../../shared/components/pagination/pagination.component';

@Component({
  selector: 'app-marketplace',
  imports: [
    SidebarLayoutComponent,
    CommonModule,
    SearchBarComponent,
    FormsModule,
    CommonModule,
    PaginationComponent,
  ],
  templateUrl: './marketplace.component.html',
  styleUrl: './marketplace.component.css',
})
export class MarketplaceComponent {
  // just for testing. To delete later
  bugList: Task[] = [
    {
      title: 'Fix login bug',
      date: new Date('2024-12-01'),
      companyName: 'OpenAI',
      techStack: ['React', 'Node.js', 'TypeScript'],
      status: 'resolved',
      reward: 500,
      difficulty: 'medium',
    },
    {
      title: 'Implement dark mode',
      date: new Date('2025-01-15'),
      companyName: 'Google',
      techStack: ['Vue.js', 'SCSS'],
      status: 'pending',
      reward: 750,
      difficulty: 'easy',
    },
    {
      title: 'Optimize database queries',
      date: new Date('2024-11-10'),
      companyName: 'Amazon',
      techStack: ['PostgreSQL', 'Python', 'Redis'],
      status: 'claimed',
      reward: 1200,
      difficulty: 'hard',
    },
    {
      title: 'Design notification system',
      date: new Date('2025-03-03'),
      companyName: 'Meta',
      techStack: ['Kafka', 'Go', 'Docker'],
      status: 'resolved',
      reward: 2000,
      difficulty: 'expert',
    },
    {
      title: 'Refactor user profile module',
      date: new Date('2025-02-20'),
      companyName: 'Microsoft',
      techStack: ['C#', '.NET', 'Azure'],
      status: 'pending',
      reward: 900,
      difficulty: 'medium',
    },
    {
      title: 'Fix payment processing bug',
      date: new Date('2025-04-10'),
      companyName: 'Stripe',
      techStack: ['Ruby', 'Rails', 'PostgreSQL'],
      status: 'claimed',
      reward: 1300,
      difficulty: 'hard',
    },
    {
      title: 'Add biometric login support',
      date: new Date('2025-05-01'),
      companyName: 'Apple',
      techStack: ['Swift', 'iOS SDK'],
      status: 'pending',
      reward: 1800,
      difficulty: 'expert',
    },
    {
      title: 'Improve search algorithm',
      date: new Date('2025-01-25'),
      companyName: 'DuckDuckGo',
      techStack: ['Rust', 'Solr'],
      status: 'resolved',
      reward: 1000,
      difficulty: 'hard',
    },
    {
      title: 'Fix layout issues on mobile',
      date: new Date('2024-12-15'),
      companyName: 'Airbnb',
      techStack: ['React Native', 'CSS'],
      status: 'pending',
      reward: 600,
      difficulty: 'easy',
    },
    {
      title: 'Implement real-time chat',
      date: new Date('2025-02-05'),
      companyName: 'Slack',
      techStack: ['Node.js', 'WebSocket', 'MongoDB'],
      status: 'claimed',
      reward: 1500,
      difficulty: 'hard',
    },
    {
      title: 'Audit security vulnerabilities',
      date: new Date('2025-03-12'),
      companyName: 'Cloudflare',
      techStack: ['Python', 'Linux', 'Security Tools'],
      status: 'resolved',
      reward: 2200,
      difficulty: 'expert',
    },
    {
      title: 'Add localization for Spanish',
      date: new Date('2025-01-10'),
      companyName: 'Notion',
      techStack: ['JavaScript', 'i18n'],
      status: 'pending',
      reward: 400,
      difficulty: 'easy',
    },
    {
      title: 'Upgrade legacy API',
      date: new Date('2025-04-22'),
      companyName: 'Twitter',
      techStack: ['Scala', 'Akka', 'Kafka'],
      status: 'claimed',
      reward: 1400,
      difficulty: 'medium',
    },
    {
      title: 'Improve file upload performance',
      date: new Date('2025-03-15'),
      companyName: 'Dropbox',
      techStack: ['React', 'S3', 'Node.js'],
      status: 'pending',
      reward: 1100,
      difficulty: 'medium',
    },
    {
      title: 'Fix OAuth token expiration issue',
      date: new Date('2025-02-22'),
      companyName: 'Spotify',
      techStack: ['Python', 'Flask', 'OAuth2'],
      status: 'claimed',
      reward: 1000,
      difficulty: 'hard',
    },
    {
      title: 'Redesign mobile navigation bar',
      date: new Date('2025-01-12'),
      companyName: 'Twitter',
      techStack: ['Flutter', 'Dart'],
      status: 'resolved',
      reward: 900,
      difficulty: 'medium',
    },
    {
      title: 'Fix timezone bug in scheduler',
      date: new Date('2025-05-10'),
      companyName: 'Calendly',
      techStack: ['TypeScript', 'NestJS'],
      status: 'pending',
      reward: 750,
      difficulty: 'easy',
    },
    {
      title: 'Add multi-language support',
      date: new Date('2025-04-05'),
      companyName: 'Asana',
      techStack: ['Vue.js', 'i18n'],
      status: 'resolved',
      reward: 950,
      difficulty: 'medium',
    },
    {
      title: 'Fix broken image links on product page',
      date: new Date('2025-03-01'),
      companyName: 'Etsy',
      techStack: ['React', 'Express', 'MongoDB'],
      status: 'claimed',
      reward: 700,
      difficulty: 'easy',
    },
    {
      title: 'Build CSV export feature',
      date: new Date('2025-05-05'),
      companyName: 'Notion',
      techStack: ['JavaScript', 'Node.js'],
      status: 'pending',
      reward: 600,
      difficulty: 'easy',
    },
    {
      title: 'Fix notification duplication bug',
      date: new Date('2025-04-20'),
      companyName: 'Slack',
      techStack: ['Go', 'Redis', 'gRPC'],
      status: 'resolved',
      reward: 1500,
      difficulty: 'hard',
    },
    {
      title: 'Implement user audit logs',
      date: new Date('2025-02-14'),
      companyName: 'GitHub',
      techStack: ['Ruby on Rails', 'PostgreSQL'],
      status: 'pending',
      reward: 1700,
      difficulty: 'hard',
    },
    {
      title: 'Improve real-time collaboration sync',
      date: new Date('2025-05-01'),
      companyName: 'Figma',
      techStack: ['WebSocket', 'TypeScript', 'Elixir'],
      status: 'claimed',
      reward: 2000,
      difficulty: 'expert',
    },

    {
      title: 'Redesign dashboard UI',
      date: new Date('2025-03-30'),
      companyName: 'Figma',
      techStack: ['TypeScript', 'React', 'TailwindCSS'],
      status: 'pending',
      reward: 1100,
      difficulty: 'medium',
    },
    {
      title: 'Build browser extension',
      date: new Date('2025-02-14'),
      companyName: 'Grammarly',
      techStack: ['JavaScript', 'Chrome API'],
      status: 'resolved',
      reward: 850,
      difficulty: 'medium',
    },
    {
      title: 'Fix infinite scroll crash',
      date: new Date('2025-05-04'),
      companyName: 'LinkedIn',
      techStack: ['Angular', 'RxJS'],
      status: 'pending',
      reward: 950,
      difficulty: 'medium',
    },
    {
      title: 'Migrate backend to serverless',
      date: new Date('2025-03-21'),
      companyName: 'Netlify',
      techStack: ['Node.js', 'AWS Lambda'],
      status: 'claimed',
      reward: 1600,
      difficulty: 'hard',
    },
    {
      title: 'Add two-factor authentication',
      date: new Date('2025-01-28'),
      companyName: 'Dropbox',
      techStack: ['Python', 'Flask', 'Twilio'],
      status: 'resolved',
      reward: 1250,
      difficulty: 'hard',
    },
    {
      title: 'Fix memory leak in image processor',
      date: new Date('2024-11-30'),
      companyName: 'Canva',
      techStack: ['C++', 'OpenCV'],
      status: 'pending',
      reward: 2100,
      difficulty: 'expert',
    },
    {
      title: 'Improve video compression quality',
      date: new Date('2025-02-07'),
      companyName: 'Zoom',
      techStack: ['C', 'FFmpeg'],
      status: 'claimed',
      reward: 1900,
      difficulty: 'expert',
    },
    {
      title: 'Add autocomplete to search bar',
      date: new Date('2025-04-01'),
      companyName: 'Pinterest',
      techStack: ['React', 'Elasticsearch'],
      status: 'pending',
      reward: 700,
      difficulty: 'easy',
    },
    {
      title: 'Create CI/CD pipeline',
      date: new Date('2025-03-09'),
      companyName: 'GitLab',
      techStack: ['Docker', 'GitLab CI', 'Kubernetes'],
      status: 'resolved',
      reward: 1500,
      difficulty: 'hard',
    },
    {
      title: 'Fix broken email notifications',
      date: new Date('2025-02-16'),
      companyName: 'Trello',
      techStack: ['Node.js', 'SendGrid'],
      status: 'claimed',
      reward: 850,
      difficulty: 'medium',
    },
    {
      title: 'Refactor frontend state management',
      date: new Date('2025-01-05'),
      companyName: 'Reddit',
      techStack: ['React', 'Redux Toolkit'],
      status: 'pending',
      reward: 1000,
      difficulty: 'medium',
    },
    {
      title: 'Integrate Stripe billing',
      date: new Date('2025-04-18'),
      companyName: 'Shopify',
      techStack: ['Ruby', 'Stripe API'],
      status: 'resolved',
      reward: 1300,
      difficulty: 'hard',
    },
  ];
  paginatedBugList: Task[] = [];

  searchQuery: string = '';

  ngOnInit() {
    this.updatePaginatedItems();
  }

  // Pagination
  bugsPerPage: number = 5;
  currentPage: number = 1;

  OnQueryChange(value: string) {
    this.searchQuery = value;
    console.log(this.searchQuery);
  }

  onPageChange(newPage: number): void {
    this.currentPage = newPage;
    this.updatePaginatedItems();
  }

  updatePaginatedItems(): void {
    console.log('clicked!');
    const start = (this.currentPage - 1) * this.bugsPerPage;
    const end = start + this.bugsPerPage;
    this.paginatedBugList = this.bugList.slice(start, end);
  }
}

// just for testing. To delete later
interface Task {
  title: string;
  date: Date;
  companyName: string;
  techStack: string[];
  status: 'resolved' | 'pending' | 'claimed';
  reward: number;
  difficulty: 'easy' | 'medium' | 'hard' | 'expert';
}
