import { vitest } from 'vitest';
import { type MountingOptions, shallowMount } from '@vue/test-utils';
import sinon, { type SinonStubbedInstance } from 'sinon';
import { type RouteLocation } from 'vue-router';

import FocusSessionDetails from './focus-session-details.vue';
import FocusSessionService from './focus-session.service';
import AlertService from '@/shared/alert/alert.service';

type FocusSessionDetailsComponentType = InstanceType<typeof FocusSessionDetails>;

let route: Partial<RouteLocation>;
const routerGoMock = vitest.fn();

vitest.mock('vue-router', () => ({
  useRoute: () => route,
  useRouter: () => ({ go: routerGoMock }),
}));

const focusSessionSample = { id: 123 };

describe('Component Tests', () => {
  let alertService: AlertService;

  afterEach(() => {
    vitest.resetAllMocks();
  });

  describe('FocusSession Management Detail Component', () => {
    let focusSessionServiceStub: SinonStubbedInstance<FocusSessionService>;
    let mountOptions: MountingOptions<FocusSessionDetailsComponentType>['global'];

    beforeEach(() => {
      route = {};
      focusSessionServiceStub = sinon.createStubInstance<FocusSessionService>(FocusSessionService);

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
          focusSessionService: () => focusSessionServiceStub,
        },
      };
    });

    describe('Navigate to details', () => {
      it('Should call load all on init', async () => {
        // GIVEN
        focusSessionServiceStub.find.resolves(focusSessionSample);
        route = {
          params: {
            focusSessionId: `${123}`,
          },
        };
        const wrapper = shallowMount(FocusSessionDetails, { global: mountOptions });
        const comp = wrapper.vm;
        // WHEN
        await comp.$nextTick();

        // THEN
        expect(comp.focusSession).toMatchObject(focusSessionSample);
      });
    });

    describe('Previous state', () => {
      it('Should go previous state', async () => {
        focusSessionServiceStub.find.resolves(focusSessionSample);
        const wrapper = shallowMount(FocusSessionDetails, { global: mountOptions });
        const comp = wrapper.vm;
        await comp.$nextTick();

        comp.previousState();
        await comp.$nextTick();

        expect(routerGoMock).toHaveBeenCalledWith(-1);
      });
    });
  });
});
