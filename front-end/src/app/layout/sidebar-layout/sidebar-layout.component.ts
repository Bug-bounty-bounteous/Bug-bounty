import { Component, Input } from '@angular/core';
import { SideBarComponent } from '../side-bar/side-bar.component';

@Component({
  selector: 'app-sidebar-layout',
  standalone: true,
  imports: [SideBarComponent],
  templateUrl: './sidebar-layout.component.html',
  styleUrl: './sidebar-layout.component.css',
})
export class SidebarLayoutComponent {
  @Input() activeSection: string = '';
}
