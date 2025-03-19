import { vitest } from 'vitest';
import { type MountingOptions, shallowMount } from '@vue/test-utils';
import sinon, { type SinonStubbedInstance } from 'sinon';
import { type RouteLocation } from 'vue-router';

import TaskDetails from './task-details.vue';
import TaskService from './task.service';
import AlertService from '@/shared/alert/alert.service';

type TaskDetailsComponentType = InstanceType<typeof TaskDetails>;

let route: Partial<RouteLocation>;
const routerGoMock = vitest.fn();

vitest.mock('vue-router', () => ({
  useRoute: () => route,
  useRouter: () => ({ go: routerGoMock }),
}));

const taskSample = { id: 123 };

describe('Component Tests', () => {
  let alertService: AlertService;

  afterEach(() => {
    vitest.resetAllMocks();
  });

  describe('Task Management Detail Component', () => {
    let taskServiceStub: SinonStubbedInstance<TaskService>;
    let mountOptions: MountingOptions<TaskDetailsComponentType>['global'];

    beforeEach(() => {
      route = {};
      taskServiceStub = sinon.createStubInstance<TaskService>(TaskService);

      alertService = new AlertService({
        bvToast: {
          toast: vitest.fn(),
        } as any,
      });

      mountOptions = {
        stubs: {
          'font-awesome-icon': true,
          'router-link': true,
        },
        provide: {
          alertService,
          taskService: () => taskServiceStub,
        },
      };
    });

    describe('Navigate to details', () => {
      it('Should call load all on init', async () => {
        // GIVEN
        taskServiceStub.find.resolves(taskSample);
        route = {
          params: {
            taskId: `${123}`,
          },
        };
        const wrapper = shallowMount(TaskDetails, { global: mountOptions });
        const comp = wrapper.vm;
        // WHEN
        await comp.$nextTick();

        // THEN
        expect(comp.task).toMatchObject(taskSample);
      });
    });

    describe('Previous state', () => {
      it('Should go previous state', async () => {
        taskServiceStub.find.resolves(taskSample);
        const wrapper = shallowMount(TaskDetails, { global: mountOptions });
        const comp = wrapper.vm;
        await comp.$nextTick();

        comp.previousState();
        await comp.$nextTick();

        expect(routerGoMock).toHaveBeenCalledWith(-1);
      });
    });
  });
});
