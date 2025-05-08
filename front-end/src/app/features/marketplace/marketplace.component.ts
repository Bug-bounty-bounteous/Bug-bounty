import { Component } from '@angular/core';
import { SidebarLayoutComponent } from '../../layout/sidebar-layout/sidebar-layout.component';
import { BugListComponent } from '../bugs/bug-list/bug-list.component';

@Component({
  selector: 'app-marketplace',
  standalone: true,
  imports: [SidebarLayoutComponent, BugListComponent],
  templateUrl: './marketplace.component.html',
  styleUrl: './marketplace.component.css',
})
export class MarketplaceComponent {}