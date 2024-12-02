import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';
import { of } from 'rxjs';
import { TaskDetailComponent } from './task-detail.component';
import { TaskService } from '../../services/task.service';
import { Task } from '../../models/task.model';
import { FormsModule } from '@angular/forms';
import { provideHttpClient } from '@angular/common/http';
import { withFetch } from '@angular/common/http';

describe('TaskDetailComponent', () => {
  let component: TaskDetailComponent;
  let fixture: ComponentFixture<TaskDetailComponent>;
  let taskService: jasmine.SpyObj<TaskService>;
  let router: jasmine.SpyObj<Router>;
  let route: Partial<ActivatedRoute>;

  const mockTask: Task = {
    id: '1',
    label: 'Test Task',
    description: 'Test Description',
    completed: false
  };

  beforeEach(async () => {
    taskService = jasmine.createSpyObj('TaskService', ['getTaskById', 'addTask', 'updateTaskStatus']);
    router = jasmine.createSpyObj('Router', ['navigate']);

    await TestBed.configureTestingModule({
      imports: [
        TaskDetailComponent,
        FormsModule
      ],
      providers: [
        provideHttpClient(withFetch()),
        { provide: TaskService, useValue: taskService },
        { provide: Router, useValue: router },
        { provide: ActivatedRoute, useValue: route }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(TaskDetailComponent);
    component = fixture.componentInstance;

  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should load existing task on init', () => {
    spyOn<any>(component, 'getTaskIdFromRoute').and.returnValue('1');
    taskService.getTaskById.and.returnValue(of(mockTask));
    fixture.detectChanges();

    expect(taskService.getTaskById).toHaveBeenCalledWith('1');
    expect(component.task).toEqual(mockTask);
    expect(component.isNewTask).toBeFalse();

  });

  it('should save new task successfully', () => {
    spyOn<any>(component, 'getTaskIdFromRoute').and.returnValue('new');
    component.isNewTask = true;
    component.task = mockTask;
    taskService.addTask.and.returnValue(of(mockTask));
    router.navigate.and.returnValue(Promise.resolve(true));

    component.saveTask();

    expect(taskService.addTask).toHaveBeenCalledWith(mockTask);
    expect(router.navigate).toHaveBeenCalled();
  });

  it('should update task status successfully', () => {
    spyOn<any>(component, 'getTaskIdFromRoute').and.returnValue('1');
    component.task = mockTask;
    taskService.updateTaskStatus.and.returnValue(of(void 0));

    component.updateStatus();

    expect(taskService.updateTaskStatus).toHaveBeenCalledWith(mockTask.id);
    expect(component.task.completed).toBeTrue();
  });

  it('should navigate back', () => {
    component.goBack();
    expect(router.navigate).toHaveBeenCalledWith(['/tasks']);
  });
});