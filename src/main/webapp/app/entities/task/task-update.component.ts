import { type Ref, computed, defineComponent, inject, ref } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { useVuelidate } from '@vuelidate/core';

import TaskService from './task.service';
import { useDateFormat, useValidation } from '@/shared/composables';
import { useAlertService } from '@/shared/alert/alert.service';

import UserService from '@/entities/user/user.service';
import GoalService from '@/entities/goal/goal.service';
import { type IGoal } from '@/shared/model/goal.model';
import { type ITask, Task } from '@/shared/model/task.model';
import { TaskStatus } from '@/shared/model/enumerations/task-status.model';

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'TaskUpdate',
  setup() {
    const taskService = inject('taskService', () => new TaskService());
    const alertService = inject('alertService', () => useAlertService(), true);

    const task: Ref<ITask> = ref(new Task());
    const userService = inject('userService', () => new UserService());
    const users: Ref<Array<any>> = ref([]);

    const goalService = inject('goalService', () => new GoalService());

    const goals: Ref<IGoal[]> = ref([]);
    const taskStatusValues: Ref<string[]> = ref(Object.keys(TaskStatus));
    const isSaving = ref(false);
    const currentLanguage = inject('currentLanguage', () => computed(() => navigator.language ?? 'zh-cn'), true);

    const route = useRoute();
    const router = useRouter();

    const previousState = () => router.go(-1);

    const retrieveTask = async taskId => {
      try {
        const res = await taskService().find(taskId);
        res.createdAt = new Date(res.createdAt);
        res.updatedAt = new Date(res.updatedAt);
        task.value = res;
      } catch (error) {
        alertService.showHttpError(error.response);
      }
    };

    if (route.params?.taskId) {
      retrieveTask(route.params.taskId);
    }

    const initRelationships = () => {
      userService()
        .retrieve()
        .then(res => {
          users.value = res.data;
        });
      goalService()
        .retrieve()
        .then(res => {
          goals.value = res.data;
        });
    };

    initRelationships();

    const validations = useValidation();
    const validationRules = {
      title: {
        required: validations.required('本字段不能为空.'),
        maxLength: validations.maxLength('本字段最大长度为 100 个字符.', 100),
      },
      description: {
        maxLength: validations.maxLength('本字段最大长度为 500 个字符.', 500),
      },
      status: {
        required: validations.required('本字段不能为空.'),
      },
      dueDate: {},
      priority: {
        required: validations.required('本字段不能为空.'),
        integer: validations.integer('本字段必须为数字.'),
      },
      createdAt: {
        required: validations.required('本字段不能为空.'),
      },
      updatedAt: {
        required: validations.required('本字段不能为空.'),
      },
      reminders: {},
      user: {},
      goal: {},
    };
    const v$ = useVuelidate(validationRules, task as any);
    v$.value.$validate();

    return {
      taskService,
      alertService,
      task,
      previousState,
      taskStatusValues,
      isSaving,
      currentLanguage,
      users,
      goals,
      v$,
      ...useDateFormat({ entityRef: task }),
    };
  },
  created(): void {},
  methods: {
    save(): void {
      this.isSaving = true;
      if (this.task.id) {
        this.taskService()
          .update(this.task)
          .then(param => {
            this.isSaving = false;
            this.previousState();
            this.alertService.showInfo(`A Task is updated with identifier ${param.id}`);
          })
          .catch(error => {
            this.isSaving = false;
            this.alertService.showHttpError(error.response);
          });
      } else {
        this.taskService()
          .create(this.task)
          .then(param => {
            this.isSaving = false;
            this.previousState();
            this.alertService.showSuccess(`A Task is created with identifier ${param.id}`);
          })
          .catch(error => {
            this.isSaving = false;
            this.alertService.showHttpError(error.response);
          });
      }
    },
  },
});
