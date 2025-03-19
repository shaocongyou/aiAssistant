import { vitest } from 'vitest';
import { type MountingOptions, shallowMount } from '@vue/test-utils';
import sinon, { type SinonStubbedInstance } from 'sinon';
import { type RouteLocation } from 'vue-router';

import ReminderDetails from './reminder-details.vue';
import ReminderService from './reminder.service';
import AlertService from '@/shared/alert/alert.service';

type ReminderDetailsComponentType = InstanceType<typeof ReminderDetails>;

let route: Partial<RouteLocation>;
const routerGoMock = vitest.fn();

vitest.mock('vue-router', () => ({
  useRoute: () => route,
  useRouter: () => ({ go: routerGoMock }),
}));

const reminderSample = { id: 123 };

describe('Component Tests', () => {
  let alertService: AlertService;

  afterEach(() => {
    vitest.resetAllMocks();
  });

  describe('Reminder Management Detail Component', () => {
    let reminderServiceStub: SinonStubbedInstance<ReminderService>;
    let mountOptions: MountingOptions<ReminderDetailsComponentType>['global'];

    beforeEach(() => {
      route = {};
      reminderServiceStub = sinon.createStubInstance<ReminderService>(ReminderService);

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
          reminderService: () => reminderServiceStub,
        },
      };
    });

    describe('Navigate to details', () => {
      it('Should call load all on init', async () => {
        // GIVEN
        reminderServiceStub.find.resolves(reminderSample);
        route = {
          params: {
            reminderId: `${123}`,
          },
        };
        const wrapper = shallowMount(ReminderDetails, { global: mountOptions });
        const comp = wrapper.vm;
        // WHEN
        await comp.$nextTick();

        // THEN
        expect(comp.reminder).toMatchObject(reminderSample);
      });
    });

    describe('Previous state', () => {
      it('Should go previous state', async () => {
        reminderServiceStub.find.resolves(reminderSample);
        const wrapper = shallowMount(ReminderDetails, { global: mountOptions });
        const comp = wrapper.vm;
        await comp.$nextTick();

        comp.previousState();
        await comp.$nextTick();

        expect(routerGoMock).toHaveBeenCalledWith(-1);
      });
    });
  });
});
