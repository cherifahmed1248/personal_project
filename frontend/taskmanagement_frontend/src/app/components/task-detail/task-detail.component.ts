import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatButtonModule } from '@angular/material/button';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { TaskService } from '../../services/task.service';
import { Task } from '../../models/task.model';
import { MatIconModule } from '@angular/material/icon';
@Component({
  selector: 'app-task-detail',
  standalone: true,
  imports: [CommonModule, MatButtonModule, MatIconModule, FormsModule],
  templateUrl: './task-detail.component.html',
  styleUrl: './task-detail.component.scss'
})
export class TaskDetailComponent implements OnInit {
  task: Task = {
    id: '',
    label: '',
    description: '',
    completed: false
  };
  isNewTask = true;
  canToggleComplete = true;
  isReady: boolean = false;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private taskService: TaskService
  ) { }

  ngOnInit(): void {
    const id = this.getTaskIdFromRoute();
    if (id === 'new') {
      this.isNewTask = true;
      this.isReady = true;
    } else if (id) {
      this.isNewTask = false;
      this.taskService.getTaskById(id).subscribe({
        next: (task) => {
            this.task = task;
            this.isReady = true;
          },
        error: (error) => console.error('Error loading task:', error)
      });
    }
  }

  private getTaskIdFromRoute() {
    return this.route.snapshot.paramMap.get('id');
  }

  saveTask(): void {
    if (this.isNewTask) {
      this.taskService.addTask(this.task).subscribe({
        next: () => this.goBack(),
        error: (error) => console.error('Error adding task:', error)
      });
    } else {
      this.taskService.updateTaskStatus(this.task.id).subscribe({
        next: () => {
          this.goBack();
        },
        error: (error) => console.error('Error updating task:', error)
      });
    }
  }

  updateStatus(): void {
    if (this.task) {
      this.taskService.updateTaskStatus(this.task.id).subscribe({
        next: () => this.task!.completed = true,
        error: (error) => console.error('Error updating status:', error)
      });
    }
  }

  goBack(): void {
    this.router.navigate(['/tasks']);
  }
}