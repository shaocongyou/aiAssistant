import { vitest } from 'vitest';
import { type MountingOptions, shallowMount } from '@vue/test-utils';
import sinon, { type SinonStubbedInstance } from 'sinon';
import { type RouteLocation } from 'vue-router';

import JournalEntryDetails from './journal-entry-details.vue';
import JournalEntryService from './journal-entry.service';
import AlertService from '@/shared/alert/alert.service';

type JournalEntryDetailsComponentType = InstanceType<typeof JournalEntryDetails>;

let route: Partial<RouteLocation>;
const routerGoMock = vitest.fn();

vitest.mock('vue-router', () => ({
  useRoute: () => route,
  useRouter: () => ({ go: routerGoMock }),
}));

const journalEntrySample = { id: 123 };

describe('Component Tests', () => {
  let alertService: AlertService;

  afterEach(() => {
    vitest.resetAllMocks();
  });

  describe('JournalEntry Management Detail Component', () => {
    let journalEntryServiceStub: SinonStubbedInstance<JournalEntryService>;
    let mountOptions: MountingOptions<JournalEntryDetailsComponentType>['global'];

    beforeEach(() => {
      route = {};
      journalEntryServiceStub = sinon.createStubInstance<JournalEntryService>(JournalEntryService);

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
          journalEntryService: () => journalEntryServiceStub,
        },
      };
    });

    describe('Navigate to details', () => {
      it('Should call load all on init', async () => {
        // GIVEN
        journalEntryServiceStub.find.resolves(journalEntrySample);
        route = {
          params: {
            journalEntryId: `${123}`,
          },
        };
        const wrapper = shallowMount(JournalEntryDetails, { global: mountOptions });
        const comp = wrapper.vm;
        // WHEN
        await comp.$nextTick();

        // THEN
        expect(comp.journalEntry).toMatchObject(journalEntrySample);
      });
    });

    describe('Previous state', () => {
      it('Should go previous state', async () => {
        journalEntryServiceStub.find.resolves(journalEntrySample);
        const wrapper = shallowMount(JournalEntryDetails, { global: mountOptions });
        const comp = wrapper.vm;
        await comp.$nextTick();

        comp.previousState();
        await comp.$nextTick();

        expect(routerGoMock).toHaveBeenCalledWith(-1);
      });
    });
  });
});
