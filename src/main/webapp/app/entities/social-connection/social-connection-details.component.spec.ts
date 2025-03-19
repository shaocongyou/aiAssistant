import { vitest } from 'vitest';
import { type MountingOptions, shallowMount } from '@vue/test-utils';
import sinon, { type SinonStubbedInstance } from 'sinon';
import { type RouteLocation } from 'vue-router';

import SocialConnectionDetails from './social-connection-details.vue';
import SocialConnectionService from './social-connection.service';
import AlertService from '@/shared/alert/alert.service';

type SocialConnectionDetailsComponentType = InstanceType<typeof SocialConnectionDetails>;

let route: Partial<RouteLocation>;
const routerGoMock = vitest.fn();

vitest.mock('vue-router', () => ({
  useRoute: () => route,
  useRouter: () => ({ go: routerGoMock }),
}));

const socialConnectionSample = { id: 123 };

describe('Component Tests', () => {
  let alertService: AlertService;

  afterEach(() => {
    vitest.resetAllMocks();
  });

  describe('SocialConnection Management Detail Component', () => {
    let socialConnectionServiceStub: SinonStubbedInstance<SocialConnectionService>;
    let mountOptions: MountingOptions<SocialConnectionDetailsComponentType>['global'];

    beforeEach(() => {
      route = {};
      socialConnectionServiceStub = sinon.createStubInstance<SocialConnectionService>(SocialConnectionService);

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
          socialConnectionService: () => socialConnectionServiceStub,
        },
      };
    });

    describe('Navigate to details', () => {
      it('Should call load all on init', async () => {
        // GIVEN
        socialConnectionServiceStub.find.resolves(socialConnectionSample);
        route = {
          params: {
            socialConnectionId: `${123}`,
          },
        };
        const wrapper = shallowMount(SocialConnectionDetails, { global: mountOptions });
        const comp = wrapper.vm;
        // WHEN
        await comp.$nextTick();

        // THEN
        expect(comp.socialConnection).toMatchObject(socialConnectionSample);
      });
    });

    describe('Previous state', () => {
      it('Should go previous state', async () => {
        socialConnectionServiceStub.find.resolves(socialConnectionSample);
        const wrapper = shallowMount(SocialConnectionDetails, { global: mountOptions });
        const comp = wrapper.vm;
        await comp.$nextTick();

        comp.previousState();
        await comp.$nextTick();

        expect(routerGoMock).toHaveBeenCalledWith(-1);
      });
    });
  });
});
