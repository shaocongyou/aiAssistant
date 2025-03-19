import { vitest } from 'vitest';
import { type MountingOptions, shallowMount } from '@vue/test-utils';
import sinon, { type SinonStubbedInstance } from 'sinon';
import { type RouteLocation } from 'vue-router';

import MoodTrackerDetails from './mood-tracker-details.vue';
import MoodTrackerService from './mood-tracker.service';
import AlertService from '@/shared/alert/alert.service';

type MoodTrackerDetailsComponentType = InstanceType<typeof MoodTrackerDetails>;

let route: Partial<RouteLocation>;
const routerGoMock = vitest.fn();

vitest.mock('vue-router', () => ({
  useRoute: () => route,
  useRouter: () => ({ go: routerGoMock }),
}));

const moodTrackerSample = { id: 123 };

describe('Component Tests', () => {
  let alertService: AlertService;

  afterEach(() => {
    vitest.resetAllMocks();
  });

  describe('MoodTracker Management Detail Component', () => {
    let moodTrackerServiceStub: SinonStubbedInstance<MoodTrackerService>;
    let mountOptions: MountingOptions<MoodTrackerDetailsComponentType>['global'];

    beforeEach(() => {
      route = {};
      moodTrackerServiceStub = sinon.createStubInstance<MoodTrackerService>(MoodTrackerService);

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
          moodTrackerService: () => moodTrackerServiceStub,
        },
      };
    });

    describe('Navigate to details', () => {
      it('Should call load all on init', async () => {
        // GIVEN
        moodTrackerServiceStub.find.resolves(moodTrackerSample);
        route = {
          params: {
            moodTrackerId: `${123}`,
          },
        };
        const wrapper = shallowMount(MoodTrackerDetails, { global: mountOptions });
        const comp = wrapper.vm;
        // WHEN
        await comp.$nextTick();

        // THEN
        expect(comp.moodTracker).toMatchObject(moodTrackerSample);
      });
    });

    describe('Previous state', () => {
      it('Should go previous state', async () => {
        moodTrackerServiceStub.find.resolves(moodTrackerSample);
        const wrapper = shallowMount(MoodTrackerDetails, { global: mountOptions });
        const comp = wrapper.vm;
        await comp.$nextTick();

        comp.previousState();
        await comp.$nextTick();

        expect(routerGoMock).toHaveBeenCalledWith(-1);
      });
    });
  });
});
