import { vitest } from 'vitest';
import { type MountingOptions, shallowMount } from '@vue/test-utils';
import sinon, { type SinonStubbedInstance } from 'sinon';
import { type RouteLocation } from 'vue-router';

import GoalDetails from './goal-details.vue';
import GoalService from './goal.service';
import AlertService from '@/shared/alert/alert.service';

type GoalDetailsComponentType = InstanceType<typeof GoalDetails>;

let route: Partial<RouteLocation>;
const routerGoMock = vitest.fn();

vitest.mock('vue-router', () => ({
  useRoute: () => route,
  useRouter: () => ({ go: routerGoMock }),
}));

const goalSample = { id: 123 };

describe('Component Tests', () => {
  let alertService: AlertService;

  afterEach(() => {
    vitest.resetAllMocks();
  });

  describe('Goal Management Detail Component', () => {
    let goalServiceStub: SinonStubbedInstance<GoalService>;
    let mountOptions: MountingOptions<GoalDetailsComponentType>['global'];

    beforeEach(() => {
      route = {};
      goalServiceStub = sinon.createStubInstance<GoalService>(GoalService);

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
          goalService: () => goalServiceStub,
        },
      };
    });

    describe('Navigate to details', () => {
      it('Should call load all on init', async () => {
        // GIVEN
        goalServiceStub.find.resolves(goalSample);
        route = {
          params: {
            goalId: `${123}`,
          },
        };
        const wrapper = shallowMount(GoalDetails, { global: mountOptions });
        const comp = wrapper.vm;
        // WHEN
        await comp.$nextTick();

        // THEN
        expect(comp.goal).toMatchObject(goalSample);
      });
    });

    describe('Previous state', () => {
      it('Should go previous state', async () => {
        goalServiceStub.find.resolves(goalSample);
        const wrapper = shallowMount(GoalDetails, { global: mountOptions });
        const comp = wrapper.vm;
        await comp.$nextTick();

        comp.previousState();
        await comp.$nextTick();

        expect(routerGoMock).toHaveBeenCalledWith(-1);
      });
    });
  });
});
