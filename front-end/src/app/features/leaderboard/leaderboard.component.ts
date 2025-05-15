import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { LeaderboardService } from '../../core/services/leaderboard.service';
import {
  DeveloperInfo,
  Leaderboard,
} from '../../core/models/leaderboard.model';
import { SidebarLayoutComponent } from '../../layout/sidebar-layout/sidebar-layout.component';
import { Bug } from '../../core/models/bug.model';

@Component({
  selector: 'app-leaderboard',
  standalone: true,
  imports: [CommonModule, SidebarLayoutComponent],
  templateUrl: './leaderboard.component.html',
  styleUrls: ['./leaderboard.component.css'],
})
export class LeaderboardComponent implements OnInit {
  leaderboard: Leaderboard;
  developers: DeveloperInfo[] = [];
  top: number = 10;
  constructor(private readonly leaderboardService: LeaderboardService) {}

  ngOnInit(): void {
    this.loadLeaderboard();
  }

  loadLeaderboard(): void {
    this.leaderboardService.getLeaderboard().subscribe({
      next: (data) => {
        this.leaderboard = data;
        this.developers = this.leaderboard.developers;
        // console.log('Leaderboard info');
        // console.log(this.leaderboard);
      },
      error: (err) => {
        console.error('Error loading leaderboard', err);
      },
    });
  }

  getTechStacksInfo(bugs: Bug[]): { name: string; count: number }[] {
    if (bugs.length <= 0) return [];
    const counts = bugs.reduce((acc, bug) => {
      bug.techStacks.forEach((tech) => {
        acc[tech.name] = (acc[tech.name] || 0) + 1;
      });
      return acc;
    }, {} as Record<string, number>);

    return Object.entries(counts).map(([name, count]) => ({
      name,
      count,
    }));
  }
}
