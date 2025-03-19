import { vitest } from 'vitest';
import { type MountingOptions, shallowMount } from '@vue/test-utils';
import sinon, { type SinonStubbedInstance } from 'sinon';
import { type RouteLocation } from 'vue-router';

import dayjs from 'dayjs';
import TaskUpdate from './task-update.vue';
import TaskService from './task.service';
import { DATE_TIME_LONG_FORMAT } from '@/shared/composables/date-format';
import AlertService from '@/shared/alert/alert.service';

import UserService from '@/entities/user/user.service';
import GoalService from '@/entities/goal/goal.service';

type TaskUpdateComponentType = InstanceType<typeof TaskUpdate>;

let route: Partial<RouteLocation>;
const routerGoMock = vitest.fn();

vitest.mock('vue-router', () => ({
  useRoute: () => route,
  useRouter: () => ({ go: routerGoMock }),
}));

const taskSample = { id: 123 };

describe('Component Tests', () => {
  let mountOptions: MountingOptions<TaskUpdateComponentType>['global'];
  let alertService: AlertService;

  describe('Task Management Update Component', () => {
    let comp: TaskUpdateComponentType;
    let taskServiceStub: SinonStubbedInstance<TaskService>;

    beforeEach(() => {
      route = {};
      taskServiceStub = sinon.createStubInstance<TaskService>(TaskService);
      taskServiceStub.retrieve.onFirstCall().resolves(Promise.resolve([]));

      alertService = new AlertService({
        bvToast: {
          toast: vitest.fn(),
        } as any,
      });

      mountOptions = {
        stubs: {
          'font-awesome-icon': true,
          'b-input-group': true,
          'b-input-group-prepend': true,
          'b-form-datepicker': true,
          'b-form-input': true,
        },
        provide: {
          alertService,
          taskService: () => taskServiceStub,

          userService: () =>
            sinon.createStubInstance<UserService>(UserService, {
              retrieve: sinon.stub().resolves({}),
            } as any),
          goalService: () =>
            sinon.createStubInstance<GoalService>(GoalService, {
              retrieve: sinon.stub().resolves({}),
            } as any),
        },
      };
    });

    afterEach(() => {
      vitest.resetAllMocks();
    });

    describe('load', () => {
      beforeEach(() => {
        const wrapper = shallowMount(TaskUpdate, { global: mountOptions });
        comp = wrapper.vm;
      });
      it('Should convert date from string', () => {
        // GIVEN
        const date = new Date('2019-10-15T11:42:02Z');

        // WHEN
        const convertedDate = comp.convertDateTimeFromServer(date);

        // THEN
        expect(convertedDate).toEqual(dayjs(date).format(DATE_TIME_LONG_FORMAT));
      });

      it('Should not convert date if date is not present', () => {
        expect(comp.convertDateTimeFromServer(null)).toBeNull();
      });
    });

    describe('save', () => {
      it('Should call update service on save for existing entity', async () => {
        // GIVEN
        const wrapper = shallowMount(TaskUpdate, { global: mountOptions });
        comp = wrapper.vm;
        comp.task = taskSample;
        taskServiceStub.update.resolves(taskSample);

        // WHEN
        comp.save();
        await comp.$nextTick();

        // THEN
        expect(taskServiceStub.update.calledWith(taskSample)).toBeTruthy();
        expect(comp.isSaving).toEqual(false);
      });

      it('Should call create service on save for new entity', async () => {
        // GIVEN
        const entity = {};
        taskServiceStub.create.resolves(entity);
        const wrapper = shallowMount(TaskUpdate, { global: mountOptions });
        comp = wrapper.vm;
        comp.task = entity;

        // WHEN
        comp.save();
        await comp.$nextTick();

        // THEN
        expect(taskServiceStub.create.calledWith(entity)).toBeTruthy();
        expect(comp.isSaving).toEqual(false);
      });
    });

    describe('Before route enter', () => {
      it('Should retrieve data', async () => {
        // GIVEN
        taskServiceStub.find.resolves(taskSample);
        taskServiceStub.retrieve.resolves([taskSample]);

        // WHEN
        route = {
          params: {
            taskId: `${taskSample.id}`,
          },
        };
        const wrapper = shallowMount(TaskUpdate, { global: mountOptions });
        comp = wrapper.vm;
        await comp.$nextTick();

        // THEN
        expect(comp.task).toMatchObject(taskSample);
      });
    });

    describe('Previous state', () => {
      it('Should go previous state', async () => {
        taskServiceStub.find.resolves(taskSample);
        const wrapper = shallowMount(TaskUpdate, { global: mountOptions });
        comp = wrapper.vm;
        await comp.$nextTick();

        comp.previousState();
        await comp.$nextTick();

        expect(routerGoMock).toHaveBeenCalledWith(-1);
      });
    });
  });
});
