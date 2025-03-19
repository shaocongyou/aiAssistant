import { vitest } from 'vitest';
import { type MountingOptions, shallowMount } from '@vue/test-utils';
import sinon, { type SinonStubbedInstance } from 'sinon';
import { type RouteLocation } from 'vue-router';

import HabitDetails from './habit-details.vue';
import HabitService from './habit.service';
import AlertService from '@/shared/alert/alert.service';

type HabitDetailsComponentType = InstanceType<typeof HabitDetails>;

let route: Partial<RouteLocation>;
const routerGoMock = vitest.fn();

vitest.mock('vue-router', () => ({
  useRoute: () => route,
  useRouter: () => ({ go: routerGoMock }),
}));

const habitSample = { id: 123 };

describe('Component Tests', () => {
  let alertService: AlertService;

  afterEach(() => {
    vitest.resetAllMocks();
  });

  describe('Habit Management Detail Component', () => {
    let habitServiceStub: SinonStubbedInstance<HabitService>;
    let mountOptions: MountingOptions<HabitDetailsComponentType>['global'];

    beforeEach(() => {
      route = {};
      habitServiceStub = sinon.createStubInstance<HabitService>(HabitService);

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
          habitService: () => habitServiceStub,
        },
      };
    });

    describe('Navigate to details', () => {
      it('Should call load all on init', async () => {
        // GIVEN
        habitServiceStub.find.resolves(habitSample);
        route = {
          params: {
            habitId: `${123}`,
          },
        };
        const wrapper = shallowMount(HabitDetails, { global: mountOptions });
        const comp = wrapper.vm;
        // WHEN
        await comp.$nextTick();

        // THEN
        expect(comp.habit).toMatchObject(habitSample);
      });
    });

    describe('Previous state', () => {
      it('Should go previous state', async () => {
        habitServiceStub.find.resolves(habitSample);
        const wrapper = shallowMount(HabitDetails, { global: mountOptions });
        const comp = wrapper.vm;
        await comp.$nextTick();

        comp.previousState();
        await comp.$nextTick();

        expect(routerGoMock).toHaveBeenCalledWith(-1);
      });
    });
  });
});
