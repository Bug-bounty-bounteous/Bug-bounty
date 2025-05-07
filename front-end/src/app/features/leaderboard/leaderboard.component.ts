import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { LeaderboardService } from '../../core/services/leaderboard.service';

@Component({
  selector: 'app-leaderboard',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './leaderboard.component.html',
  styleUrls: ['./leaderboard.component.css']
})
export class LeaderboardComponent implements OnInit {
  developers: any[] = [];
  
  constructor(private readonly leaderboardService: LeaderboardService) { }

  ngOnInit(): void {
    this.loadLeaderboard();
  }
  
  loadLeaderboard(): void {
    this.leaderboardService.getLeaderboard().subscribe({
      next: (data) => {
        this.developers = data;
      },
      error: (err) => {
        console.error('Error loading leaderboard', err);
      }
    });
  }
}
