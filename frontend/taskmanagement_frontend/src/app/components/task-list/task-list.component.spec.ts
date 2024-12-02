import { ComponentFixture, TestBed } from '@angular/core/testing';
import { TaskListComponent } from './task-list.component';
import { provideHttpClient } from '@angular/common/http';
import { withFetch } from '@angular/common/http';
import { TaskService } from '../../services/task.service';
import { Router } from '@angular/router';
import { MatSnackBar } from '@angular/material/snack-bar';
import { of } from 'rxjs';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { MatTableModule } from '@angular/material/table';
import { MatPaginatorModule } from '@angular/material/paginator';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';

describe('TaskListComponent', () => {
  let component: TaskListComponent;
  let fixture: ComponentFixture<TaskListComponent>;
  let taskService: jasmine.SpyObj<TaskService>;
  let snackBar: jasmine.SpyObj<MatSnackBar>;
  let router: jasmine.SpyObj<Router>;

  beforeEach(async () => {
    taskService = jasmine.createSpyObj('TaskService', ['getTasks', 'updateTaskStatus']);
    snackBar = jasmine.createSpyObj('MatSnackBar', ['open']);
    router = jasmine.createSpyObj('Router', ['navigate']);

    await TestBed.configureTestingModule({
      imports: [
        TaskListComponent,
        BrowserAnimationsModule,
        MatTableModule,
        MatPaginatorModule,
        MatFormFieldModule,
        MatInputModule,
        MatButtonModule
      ],
      providers: [
        provideHttpClient(withFetch()),
        { provide: TaskService, useValue: taskService },
        { provide: Router, useValue: router },
        { provide: MatSnackBar, useValue: snackBar }
      ]
    }).compileComponents();

    taskService.getTasks.and.returnValue(of({ content: [], totalElements: 0 }));
    
    fixture = TestBed.createComponent(TaskListComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
  
  it('should load tasks on init', () => {
    const mockTasks = [{ id: "1", label: 'Test Task', description: 'Description Task', completed: false }];
    taskService.getTasks.and.returnValue(of({ content: mockTasks, totalElements: 1 }));
    
    component.ngOnInit();
    
    expect(taskService.getTasks).toHaveBeenCalledWith(0, 5, 'all');
    component.tasks$.subscribe(tasks => {
      expect(tasks).toEqual(mockTasks);
    });
  });

  it('should navigate to new task page', () => {
    component.addTask();
    expect(router.navigate).toHaveBeenCalledWith(['/tasks', 'new']);
  });

  it('should handle page changes', () => {
    const pageEvent = { pageIndex: 1, pageSize: 10, length: 20 };
    
    component.onPageChange(pageEvent);
    
    expect(component.pageIndex).toBe(1);
    expect(component.pageSize).toBe(10);
    expect(taskService.getTasks).toHaveBeenCalledWith(1, 10, 'all');
  });

  it('should filter tasks based on status', () => {
    const mockTasks = [
      { id: "1", label: 'Test Task 1', description: 'Description Task 1', completed: false },
      { id: "2", label: 'Test Task 2', description: 'Description Task 2', completed: false }
    ];
    taskService.getTasks.and.returnValue(of({ content: mockTasks, totalElements: 2 }));
  
    component.statusFilter = 'pending';
    component.filterTasks();
  
    expect(component.pageIndex).toBe(0);
    expect(taskService.getTasks).toHaveBeenCalledWith(0, 5, 'pending');
    component.tasks$.subscribe(tasks => {
      expect(tasks).toEqual(mockTasks);
    });
  });

});
