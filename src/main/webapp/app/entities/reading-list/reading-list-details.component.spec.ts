import { vitest } from 'vitest';
import { type MountingOptions, shallowMount } from '@vue/test-utils';
import sinon, { type SinonStubbedInstance } from 'sinon';
import { type RouteLocation } from 'vue-router';

import ReadingListDetails from './reading-list-details.vue';
import ReadingListService from './reading-list.service';
import AlertService from '@/shared/alert/alert.service';

type ReadingListDetailsComponentType = InstanceType<typeof ReadingListDetails>;

let route: Partial<RouteLocation>;
const routerGoMock = vitest.fn();

vitest.mock('vue-router', () => ({
  useRoute: () => route,
  useRouter: () => ({ go: routerGoMock }),
}));

const readingListSample = { id: 123 };

describe('Component Tests', () => {
  let alertService: AlertService;

  afterEach(() => {
    vitest.resetAllMocks();
  });

  describe('ReadingList Management Detail Component', () => {
    let readingListServiceStub: SinonStubbedInstance<ReadingListService>;
    let mountOptions: MountingOptions<ReadingListDetailsComponentType>['global'];

    beforeEach(() => {
      route = {};
      readingListServiceStub = sinon.createStubInstance<ReadingListService>(ReadingListService);

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
          readingListService: () => readingListServiceStub,
        },
      };
    });

    describe('Navigate to details', () => {
      it('Should call load all on init', async () => {
        // GIVEN
        readingListServiceStub.find.resolves(readingListSample);
        route = {
          params: {
            readingListId: `${123}`,
          },
        };
        const wrapper = shallowMount(ReadingListDetails, { global: mountOptions });
        const comp = wrapper.vm;
        // WHEN
        await comp.$nextTick();

        // THEN
        expect(comp.readingList).toMatchObject(readingListSample);
      });
    });

    describe('Previous state', () => {
      it('Should go previous state', async () => {
        readingListServiceStub.find.resolves(readingListSample);
        const wrapper = shallowMount(ReadingListDetails, { global: mountOptions });
        const comp = wrapper.vm;
        await comp.$nextTick();

        comp.previousState();
        await comp.$nextTick();

        expect(routerGoMock).toHaveBeenCalledWith(-1);
      });
    });
  });
});
