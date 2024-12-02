import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterModule } from '@angular/router';
import { BehaviorSubject, catchError, finalize } from 'rxjs';

import { MatListModule } from '@angular/material/list';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { MatProgressBarModule } from '@angular/material/progress-bar';
import { MatPaginatorModule, PageEvent } from '@angular/material/paginator';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { MatChipsModule } from '@angular/material/chips';
import { MatButtonToggleModule } from '@angular/material/button-toggle';

import { Task } from '../../models/task.model';
import { TaskService } from '../../services/task.service';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-task-list',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    MatButtonToggleModule,
    RouterModule,
    MatListModule,
    MatCardModule,
    MatButtonModule,
    MatIconModule,
    MatCheckboxModule,
    MatProgressBarModule,
    MatPaginatorModule,
    MatSnackBarModule,
    MatChipsModule
  ],
  templateUrl: './task-list.component.html',
  styleUrls: ['./task-list.component.scss']
})
export class TaskListComponent implements OnInit {
  tasks$ = new BehaviorSubject<Task[]>([]);
  pageSize = 5;
  pageIndex = 0;
  totalTasks = 0;
  statusFilter = 'all';

  constructor(
    private router: Router,
    private taskService: TaskService,
    private snackBar: MatSnackBar
  ) {}

  ngOnInit(): void {
    this.loadTasks();
  }

  loadTasks(): void {
    this.taskService.getTasks(this.pageIndex, this.pageSize, this.statusFilter)
      .pipe(
        catchError(error => {
          this.snackBar.open('Error loading tasks', 'Close', { duration: 3000 });
          throw error;
        })
      )
      .subscribe(response => {
        this.tasks$.next(response.content);
        this.totalTasks = response.totalElements;
      });
  }

  onPageChange(event: PageEvent): void {
    this.pageIndex = event.pageIndex;
    this.pageSize = event.pageSize;
    this.loadTasks();
  }

  updateTaskStatus(task: Task): void {
    this.taskService.updateTaskStatus(task.id)
      .subscribe({
        next: () => {
          const updatedTasks = this.tasks$.value.map(t => 
            t.id === task.id ? { ...t, completed: !t.completed } : t
          );
          this.tasks$.next(updatedTasks);
          this.snackBar.open('Task updated successfully', 'Close', { duration: 2000 });
        },
        error: () => {
          this.snackBar.open('Error updating task', 'Close', { duration: 3000 });
        }
      });
  }

  addTask(): void {
    this.router.navigate(['/tasks', 'new']);
  }

  filterTasks(): void {
    this.pageIndex = 0;
    this.loadTasks();
  }
}